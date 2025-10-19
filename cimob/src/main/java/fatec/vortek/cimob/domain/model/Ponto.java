package fatec.vortek.cimob.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Ponto")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ponto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pontoId")
    private Long pontoId;

    @ManyToOne
    @JoinColumn(name = "regiaoId")
    private Regiao regiao;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "addr_city", length = 100)
    private String addrCity;

    @Column(name = "addr_street", length = 255)
    private String addrStreet;

    @Column(name = "addr_suburb", length = 100)
    private String addrSuburb;

    @Column(name = "bench", length = 1)
    private Character bench;

    @Column(name = "bin", length = 1)
    private Character bin;

    @Column(name = "bus", length = 1)
    private Character bus;

    @Column(name = "check_date_shelter")
    private java.time.LocalDate checkDateShelter;

    @Column(name = "covered", length = 1)
    private Character covered;

    @Column(name = "departures_board", length = 255)
    private String departuresBoard;

    @Column(name = "highway", length = 50)
    private String highway;

    @Column(name = "lit", length = 1)
    private Character lit;

    @Column(name = "local_ref", length = 100)
    private String localRef;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "name_en", length = 255)
    private String nameEn;

    @Column(name = "name_pt", length = 255)
    private String namePt;

    @Column(name = "name_signed", length = 1)
    private Character nameSigned;

    @Column(name = "network", length = 100)
    private String network;

    @Column(name = "network_short", length = 50)
    private String networkShort;

    @Column(name = "network_wikidata", length = 100)
    private String networkWikidata;

    @Column(name = "network_wikipedia", length = 100)
    private String networkWikipedia;

    @Column(name = "noname", length = 1)
    private Character noname;

    @Column(name = "note", length = 4000)
    private String note;

    @Column(name = "opening_hours", length = 100)
    private String openingHours;

    @Column(name = "operator", length = 100)
    private String operator;

    @Column(name = "outdoor_seating", length = 1)
    private Character outdoorSeating;

    @Column(name = "public_transport", length = 50)
    private String publicTransport;

    @Column(name = "public_transport_version")
    private Integer publicTransportVersion;

    @Column(name = "ref")
    private Long ref;

    @Column(name = "route_ref")
    private Long routeRef;

    @Column(name = "shelter", length = 1)
    private Character shelter;

    @Column(name = "source", length = 255)
    private String source;

    @Column(name = "tactile_paving", length = 1)
    private Character tactilePaving;

    @Column(name = "wheelchair", length = 1)
    private Character wheelchair;
}
