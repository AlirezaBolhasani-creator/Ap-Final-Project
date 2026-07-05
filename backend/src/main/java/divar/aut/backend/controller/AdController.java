package divar.aut.backend.controller;

import divar.aut.backend.entity.Ad;
import divar.aut.backend.security.UserPrincipal;
import divar.aut.backend.service.AdService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/ads")
public class AdController {
    @Autowired
    private AdService adService;

    @GetMapping
    public List<Ad> getAds(@RequestParam(defaultValue = "0") int page) {
        int pageSize = 10;
        return adService.getAllAdsPaginated(page, pageSize);
    }

    @GetMapping("/{id}")
    public Ad getAdById(@PathVariable Long id) {
        return adService.getAdById(id);
    }

    @PostMapping
    public Ad createAd(@AuthenticationPrincipal UserPrincipal principal, @Valid @RequestBody Ad ad) {
        return adService.saveAd(principal.getUser(), ad);
    }

    @PostMapping("/{id}/image")
    public Ad uploadImage(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal,
                           @RequestParam("file") MultipartFile file) {
        return adService.uploadAdImage(id, file, principal.getUser());
    }

    /** Admin-only: enforced by SecurityConfig, not here. */
    @GetMapping("/pending")
    public List<Ad> getPendingAds(@RequestParam(defaultValue = "0") int page) {
        int pageSize = 10;
        Pageable pageable = PageRequest.of(page, pageSize);
        return adService.getPendingAds(pageable);
    }

    /** Admin-only: enforced by SecurityConfig, not here. */
    @PutMapping("/{id}/status")
    public Ad updateAdStatus(@PathVariable Long id, @RequestParam String status) {
        return adService.changeAdStatus(id, status);
    }
}
