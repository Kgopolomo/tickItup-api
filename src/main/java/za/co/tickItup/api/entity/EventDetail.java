package za.co.tickItup.api.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "events_details")
public class EventDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int ageRestrictions;

    private boolean hasRefundPolicy;

    private String parkingInformation;

    private String accessibilityInformation;

    private String eventRules;



}
