package divar.aut.backend.service;

import divar.aut.backend.dto.MetadataDeleteRequest;
import divar.aut.backend.dto.MetadataDeleteStrategy;
import divar.aut.backend.entity.Ad;
import divar.aut.backend.entity.Category;
import divar.aut.backend.entity.City;
import divar.aut.backend.entity.Conversation;
import divar.aut.backend.exception.ApiException;
import divar.aut.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Service
public class MetadataDeletionService {
    private final AdRepository adRepository;
    private final CategoryRepository categoryRepository;
    private final CityRepository cityRepository;
    private final FavoriteRepository favoriteRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final SellerRatingRepository sellerRatingRepository;
    private final ImageStorageService imageStorageService;

    public MetadataDeletionService(AdRepository adRepository,
                                   CategoryRepository categoryRepository,
                                   CityRepository cityRepository,
                                   FavoriteRepository favoriteRepository,
                                   ConversationRepository conversationRepository,
                                   MessageRepository messageRepository,
                                   SellerRatingRepository sellerRatingRepository,
                                   ImageStorageService imageStorageService) {
        this.adRepository = adRepository;
        this.categoryRepository = categoryRepository;
        this.cityRepository = cityRepository;
        this.favoriteRepository = favoriteRepository;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.sellerRatingRepository = sellerRatingRepository;
        this.imageStorageService = imageStorageService;
    }

    public long countCategoryAds(Long categoryId) {
        requireCategory(categoryId);
        return adRepository.countByCategoryId(categoryId);
    }

    public long countCityAds(Long cityId) {
        requireCity(cityId);
        return adRepository.countByCityId(cityId);
    }

    @Transactional
    public void deleteCategory(Long categoryId, MetadataDeleteRequest request) {
        Category category = requireCategory(categoryId);
        List<Ad> ads = adRepository.findByCategoryId(categoryId);
        if (ads.isEmpty()) {
            categoryRepository.delete(category);
            return;
        }
        if (request.getStrategy() == MetadataDeleteStrategy.REASSIGN) {
            Category replacement = requireReplacementCategory(categoryId, request.getReplacementId());
            ads.forEach(ad -> ad.setCategory(replacement));
            adRepository.saveAll(ads);
        } else {
            permanentlyDeleteAds(ads);
        }
        categoryRepository.delete(category);
    }

    @Transactional
    public void deleteCity(Long cityId, MetadataDeleteRequest request) {
        City city = requireCity(cityId);
        List<Ad> ads = adRepository.findByCityId(cityId);
        if (ads.isEmpty()) {
            cityRepository.delete(city);
            return;
        }
        if (request.getStrategy() == MetadataDeleteStrategy.REASSIGN) {
            City replacement = requireReplacementCity(cityId, request.getReplacementId());
            ads.forEach(ad -> ad.setCity(replacement));
            adRepository.saveAll(ads);
        } else {
            permanentlyDeleteAds(ads);
        }
        cityRepository.delete(city);
    }

    private void permanentlyDeleteAds(List<Ad> ads) {
        List<String> imageFileNames = ads.stream()
                .flatMap(ad -> ad.getImages().stream())
                .map(image -> image.getFileName())
                .toList();
        List<Conversation> conversations = conversationRepository.findByAdIn(ads);
        if (!conversations.isEmpty()) {
            messageRepository.deleteByConversationIn(conversations);
            conversationRepository.deleteAll(conversations);
        }
        favoriteRepository.deleteByAdIn(ads);
        sellerRatingRepository.deleteByAdIn(ads);
        adRepository.deleteAll(ads);
        adRepository.flush();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                imageFileNames.forEach(imageStorageService::delete);
            }
        });
    }

    private Category requireCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("دسته‌بندی مورد نظر پیدا نشد"));
    }

    private City requireCity(Long id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("شهر مورد نظر پیدا نشد"));
    }

    private Category requireReplacementCategory(Long deletedId, Long replacementId) {
        if (replacementId == null || deletedId.equals(replacementId)) {
            throw ApiException.badRequest("باید یک دسته‌بندی جایگزین متفاوت انتخاب کنید");
        }
        return requireCategory(replacementId);
    }

    private City requireReplacementCity(Long deletedId, Long replacementId) {
        if (replacementId == null || deletedId.equals(replacementId)) {
            throw ApiException.badRequest("باید یک شهر جایگزین متفاوت انتخاب کنید");
        }
        return requireCity(replacementId);
    }
}
