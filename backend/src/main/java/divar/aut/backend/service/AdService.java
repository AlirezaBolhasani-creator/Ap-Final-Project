package divar.aut.backend.service;

import divar.aut.backend.dto.AdRequest;
import divar.aut.backend.dto.AdResponse;
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
    public List<AdResponse> getPendingAds(Pageable pageable) {
        return adRepository.findByStatus("PENDING", pageable).getContent().stream()
                .map(AdResponse::new)
                .toList();
    }

    /** Admin-only status change (approve/reject); access already restricted by SecurityConfig. */
    public AdResponse changeAdStatus(Long id, String status) {
        Ad ad = adRepository.findById(id).orElseThrow(() -> ApiException.notFound("Ad not found"));
        ad.setStatus(status);
        return new AdResponse(adRepository.save(ad));
    }

    public List<AdResponse> getAllAdsPaginated(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return adRepository.findByStatus("ACTIVE", pageable).getContent().stream()
                .map(AdResponse::new)
                .toList();
    }

    public AdResponse saveAd(User owner, AdRequest request) {
        Ad ad = new Ad();
        ad.setTitle(request.getTitle());
        ad.setPrice(request.getPrice());
        ad.setLocation(request.getLocation());
        ad.setCondition(request.getCondition());
        ad.setCategory(request.getCategory());

        ad.setUser_id(owner.getUsername());
        ad.setTime(LocalDateTime.now());
        return new AdResponse(adRepository.save(ad));
    }

    public AdResponse getAdById(Long id) {
        Ad ad = adRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Ad not found with id: " + id));
        return new AdResponse(ad);
    }

    public AdResponse uploadAdImage(Long adId, MultipartFile file, User actingUser) {
        try {
            Ad ad = adRepository.findById(adId)
                    .orElseThrow(() -> ApiException.notFound("Ad not found with id: " + adId));
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
            return new AdResponse(adRepository.save(ad));
        } catch (java.io.IOException e) {
            throw new ApiException(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error in saving file: " + e.getMessage());
        }
    }
}
