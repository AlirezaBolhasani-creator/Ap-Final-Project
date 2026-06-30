package divar.aut.backend.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDateTime;

@Entity
@Table(name = "ads")
public class Ad
{
    public Long getId()
    {
        return id;
    }
    public void setId(Long id)
    {
        this.id = id;
    }
    public String getTitle()
    {
        return title;
    }
    public void setTitle(String title)
    {
        this.title = title;
    }
    public String getLocation()
    {
        return location;
    }
    public void setLocation(String location)
    {
        this.location = location;
    }
    public String getCondition()
    {
        return condition;
    }
    public void setCondition(String condition)
    {
        this.condition = condition;
    }
    public String getImageUrl()
    {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }
    public BigDecimal getPrice()
    {
        return price;
    }
    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }
    public LocalDateTime getTime()
    {
        return time;
    }
    public void setTime(LocalDateTime time)
    {
        this.time = time;
    }
    public int getPhotoCount()
    {
        return photoCount;
    }
    public void setPhotoCount(int photoCount)
    {
        this.photoCount = photoCount;
    }
    public void setUser_id(String user_id)
    {
        this.user_id = user_id;
    }
    public String getUser_id()
    {
        return user_id;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private BigDecimal price;
    @Column(nullable = false)
    private String location;
    @Column(nullable = false)
    private LocalDateTime time;
    @Column(nullable = false)
    private String condition;
    @Column(nullable = false)
    private String imageUrl;
    @Column
    private int photoCount;
    @Column(nullable = false)
    private String user_id;

}
