package divar.aut.backend.service;

import divar.aut.backend.dto.AdminStatsResponse;
import divar.aut.backend.entity.Ad;
import divar.aut.backend.entity.AdStatus;
import divar.aut.backend.entity.Role;
import divar.aut.backend.entity.User;
import divar.aut.backend.exception.ApiException;
import divar.aut.backend.repository.AdRepository;
import divar.aut.backend.repository.CategoryRepository;
import divar.aut.backend.repository.CityRepository;
import divar.aut.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link AdminService} class.
 * Tests administrative capabilities such as deleting advertisements,
 * blocking/unblocking users, protecting admin accounts from being blocked,
 * and generating platform statistics.
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private AdRepository adRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CityRepository cityRepository;

    @InjectMocks
    private AdminService adminService;

    private User regularUser;
    private User adminUser;
    private Ad ad;

    @BeforeEach
    void setUp() {
        regularUser = new User();
        regularUser.setRole(Role.USER);
        regularUser.setBlocked(false);
        ReflectionTestUtils.setField(regularUser, "id", 1L);

        adminUser = new User();
        adminUser.setRole(Role.ADMIN);
        ReflectionTestUtils.setField(adminUser, "id", 2L);

        ad = new Ad("Title", "Desc", 1000.0, null, regularUser, null, null);
        ReflectionTestUtils.setField(ad, "id", 10L);
        ReflectionTestUtils.setField(ad, "status", AdStatus.ACTIVE);
    }

    /**
     * Tests that a valid advertisement is successfully marked as deleted.
     */
    @Test
    void deleteAd_MarksAdAsDeleted_WhenAdExists() {
        when(adRepository.findById(10L)).thenReturn(Optional.of(ad));

        adminService.deleteAd(10L);

        assertEquals(AdStatus.DELETED, ad.getStatus());
        verify(adRepository, times(1)).save(ad);
    }

    /**
     * Tests that attempting to block a user with an ADMIN role throws an exception.
     */
    @Test
    void setBlocked_ThrowsException_WhenTargetIsAdmin() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));

        assertThrows(ApiException.class, () -> adminService.setBlocked(2L, true));
        verify(userRepository, never()).save(any());
    }

    /**
     * Tests that a regular user's block status can be successfully updated.
     */
    @Test
    void setBlocked_UpdatesUserStatus_WhenTargetIsRegularUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(regularUser));

        adminService.setBlocked(1L, true);

        assertTrue(regularUser.isBlocked());
        verify(userRepository, times(1)).save(regularUser);
    }

    /**
     * Tests that the platform statistics generation accurately computes totals
     * based on repository counts and entity filtering.
     */
    @Test
    void getStats_ReturnsAccurateSystemStatistics() {
        Ad pendingAd = new Ad("Pending Title", "Desc", 1000.0, null, regularUser, null, null);
        ReflectionTestUtils.setField(pendingAd, "status", AdStatus.PENDING_REVIEW);

        Ad soldAd = new Ad("Sold Title", "Desc", 1000.0, null, regularUser, null, null);
        ReflectionTestUtils.setField(soldAd, "status", AdStatus.SOLD);

        User blockedUser = new User();
        blockedUser.setBlocked(true);

        when(userRepository.findAll()).thenReturn(Arrays.asList(regularUser, adminUser, blockedUser));
        when(adRepository.findAll()).thenReturn(Arrays.asList(ad, pendingAd, soldAd));
        when(categoryRepository.count()).thenReturn(5L);
        when(cityRepository.count()).thenReturn(10L);

        AdminStatsResponse stats = adminService.getStats();

        assertEquals(3, stats.getTotalUsers());
        assertEquals(1, stats.getBlockedUsers());
        assertEquals(3, stats.getTotalAds());
        assertEquals(1, stats.getPendingAds());
        assertEquals(1, stats.getActiveAds());
        assertEquals(1, stats.getSoldAds());
        assertEquals(5L, stats.getCategories());
        assertEquals(10L, stats.getCities());
    }
}