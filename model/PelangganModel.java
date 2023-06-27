package propensi.SIJAWAH.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
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
@Table(name = "pelanggan")
public class PelangganModel implements Serializable {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String uuid;

    @NotNull
    @Column(name = "nomor_telepon", nullable = false, unique = true)
    private String nomorTelepon;

    @NotNull
    @Column(name = "nama", nullable = false)
    private String nama;

    @NotNull
    @Column(name = "jumlah_pembelian", nullable = false)
    private Integer jumlahPembelian;

    @NotNull
    @Column(name = "last_update", nullable = false)
    @DateTimeFormat(pattern="yyyy-MM-dd' 'HH:mm")
    private LocalDateTime lastUpdate;

    @OneToMany(mappedBy = "pelanggan", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<JumlahModel> listJumlah;

}

