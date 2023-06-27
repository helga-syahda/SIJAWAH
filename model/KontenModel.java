package propensi.SIJAWAH.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "konten")
public class KontenModel implements Serializable {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String uuid;

    @NotNull
    @Column(name = "nama", nullable = false)
    private String nama;

    @NotNull
    @Column(name = "link", nullable = false)
    private String link;

    @NotNull
    @Lob
    @Column(name = "caption", nullable = false)
    private String caption;

    @NotNull
    @Column(name = "tanggal_publish", nullable = false)
    @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm")
    private LocalDateTime tanggal_publish;

    @NotNull
    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "feedback", nullable = true)
    private String feedback;

    @NotNull
    @Column(name = "instagram", nullable = false)
    private Boolean instagram;

    @NotNull
    @Column(name = "whatsapp", nullable = false)
    private Boolean whatsapp;

    @NotNull
    @Column(name = "twitter", nullable = false)
    private Boolean twitter;

    @ManyToOne
    @JoinColumn(name = "socmed", referencedColumnName = "uuid_user", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SocialMediaSpecialistModel socmed;
}
