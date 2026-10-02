package com.booking.hotel.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
/*
* Contracts НОВАЯ ВЕРСИЯ
contract number: INTEGER
Client of hotel_Client ID: INTEGER (FK)
Term of stay: INTEGER
date conclusion agreement: DATETIME
* */

@Entity
@Table(name = "Contracts")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class Contracts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contract number")
    private int contractNumber;
    //@Column(name = "Room numbers")
    //private int roomNumbers;
    @Column(name = "Term of stay")
    private int termOfStay;

    @Override
    public String toString() {
        return "Contracts{" +
                "contractNumber=" + contractNumber +
                ", termOfStay=" + termOfStay +
                ", dateConclusionAgreement=" + dateConclusionAgreement +
                '}';
    }

    @Column(name = "date conclusion agreement")
    private LocalDate dateConclusionAgreement;
    @ManyToOne
    @JoinColumn(name="Client of hotel_Client ID",referencedColumnName ="Client ID" )
    private ClientOfHotel clientOfHotel;
}
