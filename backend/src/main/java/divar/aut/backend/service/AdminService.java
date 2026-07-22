package divar.aut.backend.service;

import divar.aut.backend.dto.AdSummaryResponse;
import divar.aut.backend.dto.AdminStatsResponse;
import divar.aut.backend.dto.UserResponse;
import divar.aut.backend.entity.Ad;
import divar.aut.backend.entity.AdStatus;
import divar.aut.backend.entity.User;
import divar.aut.backend.exception.ApiException;
import divar.aut.backend.repository.AdRepository;
import divar.aut.backend.repository.CategoryRepository;
import divar.aut.backend.repository.CityRepository;
import divar.aut.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {
    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CityRepository cityRepository;

    public AdminService(AdRepository adRepository, UserRepository userRepository,
                        CategoryRepository categoryRepository, CityRepository cityRepository) {
        this.adRepository = adRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.cityRepository = cityRepository;
    }

    public List<AdSummaryResponse> listAllAds() {
        return adRepository.findAll().stream().map(AdSummaryResponse::new).toList();
    }

    public void deleteAd(Long id) {
        Ad ad = adRepository.findById(id).orElseThrow(() -> ApiException.notFound("آگهی مورد نظر پیدا نشد"));
        ad.markAsDeleted();
        adRepository.save(ad);
    }

    public List<UserResponse> listUsers() {
        return userRepository.findAll().stream().map(UserResponse::new).toList();
    }

    public void setBlocked(Long id, boolean blocked) {
        User user = userRepository.findById(id).orElseThrow(() -> ApiException.notFound("کاربر مورد نظر پیدا نشد"));
        if (user.isAdmin() && blocked) throw ApiException.badRequest("حساب‌های مدیر را نمی‌توان مسدود کرد");
        user.setBlocked(blocked);
        userRepository.save(user);
    }

    public AdminStatsResponse getStats() {
        List<Ad> ads = adRepository.findAll();
        List<User> users = userRepository.findAll();
        return new AdminStatsResponse(
                users.size(), users.stream().filter(User::isBlocked).count(), ads.size(),
                ads.stream().filter(ad -> ad.getStatus() == AdStatus.PENDING_REVIEW).count(),
                ads.stream().filter(ad -> ad.getStatus() == AdStatus.ACTIVE).count(),
                ads.stream().filter(ad -> ad.getStatus() == AdStatus.SOLD).count(),
                categoryRepository.count(), cityRepository.count());
    }
}
