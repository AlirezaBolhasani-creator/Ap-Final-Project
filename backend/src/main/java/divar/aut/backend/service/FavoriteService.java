package divar.aut.backend.service;

import divar.aut.backend.dto.AdSummaryResponse;
import divar.aut.backend.entity.Ad;
import divar.aut.backend.entity.Favorite;
import divar.aut.backend.entity.User;
import divar.aut.backend.exception.ApiException;
import divar.aut.backend.repository.AdRepository;
import divar.aut.backend.repository.FavoriteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final AdRepository adRepository;

    public FavoriteService(FavoriteRepository favoriteRepository, AdRepository adRepository) {
        this.favoriteRepository = favoriteRepository;
        this.adRepository = adRepository;
    }

    public List<AdSummaryResponse> listFavorites(User user) {
        return favoriteRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(favorite -> new AdSummaryResponse(favorite.getAd()))
                .toList();
    }

    public void addFavorite(User user, Long adId) {
        Ad ad = findAdOrThrow(adId);
        if (ad.getOwner().getId().equals(user.getId())) {
            throw ApiException.badRequest("You cannot add your own ad to favorites");
        }
        if (favoriteRepository.existsByUserAndAd(user, ad)) {
            throw ApiException.badRequest("This ad is already in your favorites");
        }
        favoriteRepository.save(new Favorite(user, ad));
    }

    public void removeFavorite(User user, Long adId) {
        Ad ad = findAdOrThrow(adId);
        Favorite favorite = favoriteRepository.findByUserAndAd(user, ad)
                .orElseThrow(() -> ApiException.notFound("This ad is not in your favorites"));
        favoriteRepository.delete(favorite);
    }

    private Ad findAdOrThrow(Long adId) {
        return adRepository.findById(adId)
                .orElseThrow(() -> ApiException.notFound("Advertisement not found"));
    }
}
