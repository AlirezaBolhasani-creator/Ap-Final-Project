package divar.aut.backend;

import divar.aut.backend.model.Ad;
import divar.aut.backend.service.AdService;
import divar.aut.backend.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/ads")
public class AdController {
    @Autowired
    private AdService adService;


    @GetMapping
    public List<Ad> getAds() {
        return adService.getAllAds();
    }
    @GetMapping("/{id}")
    public Ad getAdById(@PathVariable Long id) {
        return adService.getAdById(id);
    }

    @PostMapping
    public Ad createAd(@RequestHeader("Authorization") String token, @RequestBody Ad ad) {

        return adService.saveAd(token, ad);
    }
    @PostMapping("/{id}/image")
    public Ad UploadImage(@PathVariable Long id, @RequestHeader("Authorization") String token,
                          @RequestParam("file") MultipartFile file)
    {
        return adService.uploadAdImage(id, file, token);
    }


}