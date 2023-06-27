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
@Table(name = "produk")
public class ProdukModel implements Serializable {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String uuid;

    @NotNull
    @Column(name = "nama", nullable = false, unique = true)
    private String nama;

    @NotNull
    @Column(name = "harga", nullable = false)
    private Integer harga;

    @OneToMany(mappedBy = "produk", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<JumlahModel> listJumlah;

}
