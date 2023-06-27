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
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "rapat")
public class RapatModel implements Serializable {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "uuid_rapat")
    private String uuid;

    @NotNull
    @Column(name = "nama", nullable = false)
    private String nama;

    @NotNull
    @Column(name = "waktu", nullable = false)
    @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm")
    private LocalDateTime waktu;

    @NotNull
    @Column(name = "link", nullable = false)
    private String link;

    @NotNull
    @Column(name = "status", nullable = false)
    private String status;

    @Lob
    @Column(name = "feedback", nullable = true)
    private String feedback;

    @ManyToOne
    @JoinColumn(name = "pic_rapat", referencedColumnName = "uuid_user", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserModel pic;

    @ManyToMany
    @JoinTable(name = "peserta_rapat", joinColumns = @JoinColumn(name = "uuid_rapat"), inverseJoinColumns = @JoinColumn(name = "uuid_user"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<UserModel> listPeserta;

    public String getTanggalRapat() {
        // Format tanggal
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("id", "ID"));

        // Mendapatkan nama hari dalam bahasa Indonesia
        String dayName = this.waktu.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("id", "ID"));

        // Menggabungkan nama hari dan format tanggal menjadi string
        return String.format("%s, %s", dayName, this.waktu.format(dateFormatter));
    }

    public String getWaktuRapat() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        return this.waktu.format(timeFormatter);
    }
}
