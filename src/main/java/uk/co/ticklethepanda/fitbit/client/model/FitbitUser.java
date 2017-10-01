package uk.co.ticklethepanda.fitbit.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;

public class FitbitUser {

    @SerializedName("memberSince")
    @Expose
    private LocalDate memberSince;

    public LocalDate getMemberSince() {
        return memberSince;
    }
}
