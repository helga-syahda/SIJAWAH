package propensi.SIJAWAH.model;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "socmed")
@DiscriminatorValue("socmed")
public class SocialMediaSpecialistModel extends UserModel{

    @OneToMany(mappedBy = "socmed", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<KontenModel> konten;
}