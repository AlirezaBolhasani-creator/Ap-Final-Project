package divar.aut.backend.service;

import divar.aut.backend.model.Ad;
import divar.aut.backend.repository.AdRepository;
import divar.aut.backend.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class AdService {
    @Autowired
    private AdRepository adRepository;

    @Autowired
    private JwtUtils jwtUtils;

    public List<Ad> getAllAds() {
        return adRepository.findAll();
    }

    public Ad saveAd(String token, Ad ad) {
        String jwt = token.substring(7);

        if (!jwtUtils.validateToken(jwt)) {
            throw new RuntimeException("Invalid token");
        }

        if(ad.getTitle() == null || ad.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Title is empty");
        }
        if(ad.getPrice() == null || ad.getPrice().doubleValue() < 0) {
            throw new IllegalArgumentException("Price is invalid");
        }

        String username = jwtUtils.getUsernameFromToken(jwt);
        ad.setUser_id(username);

        return adRepository.save(ad);
    }

    public Ad getAdById(Long id) {
        return adRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ad not found with id: " + id));
    }
    public Ad uploadAdImage(Long adId, MultipartFile file, String token) {
        try{
            String jwt = token.substring(7);
            if (!jwtUtils.validateToken(jwt)) {
                throw new RuntimeException("Invalid token");
            }
            String username = jwtUtils.getUsernameFromToken(jwt);
            Ad ad = getAdById(adId);
            if(!ad.getUser_id().equals(username)) {
                throw new SecurityException("You are not allowed to access this resource");
            }
            if(file.isEmpty()) {
                throw new IllegalArgumentException("The file is empty");
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
            return adRepository.save(ad);
        }
        catch (java.io.IOException e) {
            throw new RuntimeException("Error in saving file: " + e.getMessage());
        }
    }
}