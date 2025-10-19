package fatec.vortek.cimob.presentation.dto.response;

import fatec.vortek.cimob.domain.model.Ponto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PontoResponseDTO {

    private Long pontoId;
    private Long regiaoId;
    private Double latitude;
    private Double longitude;
    private String addrCity;
    private String addrStreet;
    private String addrSuburb;
    private Character bench;
    private Character bin;
    private Character bus;
    private java.time.LocalDate checkDateShelter;
    private Character covered;
    private String departuresBoard;
    private String highway;
    private Character lit;
    private String localRef;
    private String name;
    private String nameEn;
    private String namePt;
    private Character nameSigned;
    private String network;
    private String networkShort;
    private String networkWikidata;
    private String networkWikipedia;
    private Character noname;
    private String note;
    private String openingHours;
    private String operator;
    private Character outdoorSeating;
    private String publicTransport;
    private Integer publicTransportVersion;
    private Long ref;
    private Long routeRef;
    private Character shelter;
    private String source;
    private Character tactilePaving;
    private Character wheelchair;

    public static PontoResponseDTO PontoModel2ResponseDTO(Ponto p) {
        if (p == null) return null;

        return PontoResponseDTO.builder()
                .pontoId(p.getPontoId())
                .regiaoId(p.getRegiao() != null ? p.getRegiao().getRegiaoId() : null)
                .latitude(p.getLatitude())
                .longitude(p.getLongitude())
                .addrCity(p.getAddrCity())
                .addrStreet(p.getAddrStreet())
                .addrSuburb(p.getAddrSuburb())
                .bench(p.getBench())
                .bin(p.getBin())
                .bus(p.getBus())
                .checkDateShelter(p.getCheckDateShelter())
                .covered(p.getCovered())
                .departuresBoard(p.getDeparturesBoard())
                .highway(p.getHighway())
                .lit(p.getLit())
                .localRef(p.getLocalRef())
                .name(p.getName())
                .nameEn(p.getNameEn())
                .namePt(p.getNamePt())
                .nameSigned(p.getNameSigned())
                .network(p.getNetwork())
                .networkShort(p.getNetworkShort())
                .networkWikidata(p.getNetworkWikidata())
                .networkWikipedia(p.getNetworkWikipedia())
                .noname(p.getNoname())
                .note(p.getNote())
                .openingHours(p.getOpeningHours())
                .operator(p.getOperator())
                .outdoorSeating(p.getOutdoorSeating())
                .publicTransport(p.getPublicTransport())
                .publicTransportVersion(p.getPublicTransportVersion())
                .ref(p.getRef())
                .routeRef(p.getRouteRef())
                .shelter(p.getShelter())
                .source(p.getSource())
                .tactilePaving(p.getTactilePaving())
                .wheelchair(p.getWheelchair())
                .build();
    }
}
