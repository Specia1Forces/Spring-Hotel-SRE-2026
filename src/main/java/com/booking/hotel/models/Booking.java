package com.booking.hotel.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;
/*Booking  Новая версия
Reservation ID
Client of hotel_Client ID
Settlement date: DATE
Eviction date: DATE
Total amount of the payment: INTEGER
Prepayment status
Booking status
Residence status
*
* */

@Entity
@Table(name = "Booking")

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

@EqualsAndHashCode
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Reservation ID")
    private int reservationId;
    @Column(name = "Settlement date")
    private LocalDate settlementDate;
    @Column(name = "Eviction date")
    private LocalDate evictionDate;
    @Column(name = "Total amount of the payment")
    private int totalAmountOfPayment;
    @Column(name = "Prepayment status")
    private String prepaymentStatus;
    @Column(name = "Booking status")
    private String bookingStatus;
    @Column(name = "Residence status")
    private String residenceStatus;

    @ManyToOne
    @JoinColumn(name="Client of hotel_Client ID",referencedColumnName ="Client ID" )
    private ClientOfHotel clientOfHotel1;
    @OneToOne(mappedBy = "booking")
    private Accounts accounts;
    @OneToMany(mappedBy = "booking1")
    private List<RelBetweenRoomsBooking> relBetweenRoomsBooking;



}
