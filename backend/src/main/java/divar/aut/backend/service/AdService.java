package divar.aut.backend.service;

import divar.aut.backend.entity.Ad;
import divar.aut.backend.entity.User;
import divar.aut.backend.exception.ApiException;
import divar.aut.backend.repository.AdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Business rules for creating, browsing, and moderating advertisements. Who
 * is allowed to do what is now decided by SecurityConfig (route/role
 * restrictions) and the AuthenticationPrincipal passed in by AdController -
 * this class no longer parses tokens itself.
 */
@Service
public class AdService {
    @Autowired
    private AdRepository adRepository;

    /** Admin-only ad moderation queue; access already restricted by SecurityConfig. */
    public List<Ad> getPendingAds(Pageable pageable) {
        return adRepository.findByStatus("PENDING", pageable).getContent();
    }

    /** Admin-only status change (approve/reject); access already restricted by SecurityConfig. */
    public Ad changeAdStatus(Long id, String status) {
        Ad ad = adRepository.findById(id).orElseThrow(() -> ApiException.notFound("Ad not found"));
        ad.setStatus(status);
        return adRepository.save(ad);
    }

    public List<Ad> getAllAdsPaginated(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return adRepository.findByStatus("ACTIVE", pageable).getContent();
    }

    public Ad saveAd(User owner, Ad ad) {
        if (ad.getTitle() == null || ad.getTitle().isEmpty()) {
            throw ApiException.badRequest("Title is empty");
        }
        if (ad.getPrice() == null || ad.getPrice().doubleValue() < 0) {
            throw ApiException.badRequest("Price is invalid");
        }

        ad.setUser_id(owner.getUsername());
        ad.setTime(LocalDateTime.now());
        return adRepository.save(ad);
    }

    public Ad getAdById(Long id) {
        return adRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Ad not found with id: " + id));
    }

    public Ad uploadAdImage(Long adId, MultipartFile file, User actingUser) {
        try {
            Ad ad = getAdById(adId);
            if (!ad.getUser_id().equals(actingUser.getUsername())) {
                throw ApiException.forbidden("You are not allowed to access this resource");
            }
            if (file.isEmpty()) {
                throw ApiException.badRequest("The file is empty");
            }
            String uploadDirectory = "uploads/";
            java.io.File directory = new java.io.File(uploadDirectory);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            String fileName = file.getOriginalFilename();
            String uniqueFileName = UUID.randomUUID().toString() + "." + fileName;
            java.nio.file.Path filePath = java.nio.file.Paths.get(uploadDirectory + uniqueFileName);
            java.nio.file.Files.write(filePath, file.getBytes());
            ad.setImageUrl(uniqueFileName);
            ad.setPhotoCount(ad.getPhotoCount() + 1);
            return adRepository.save(ad);
        } catch (java.io.IOException e) {
            throw new ApiException(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error in saving file: " + e.getMessage());
        }
    }
}
