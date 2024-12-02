package models;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import java.time.LocalDate;

public class Booking {
	private final SimpleIntegerProperty id;
	private final SimpleStringProperty firstname;
	private final SimpleStringProperty lastname;
	private final SimpleStringProperty email;
	private final SimpleStringProperty filmTitle;
	private final SimpleStringProperty screenNum;
	private final SimpleStringProperty date;
	private final SimpleStringProperty time;
	private final SimpleIntegerProperty adultTickets;
	private final SimpleIntegerProperty childTickets;
	private final SimpleIntegerProperty seniorTickets;
	private final SimpleDoubleProperty totalPrice;
	private final SimpleBooleanProperty isVip;

	public Booking(int id, String firstname, String lastname, String email, String filmTitle, String screenNum,
			LocalDate date, String time, int adultTickets, int childTickets, int seniorTickets, double totalPrice,
			boolean isVip) {
		this.id = new SimpleIntegerProperty(id);
		this.firstname = new SimpleStringProperty(firstname);
		this.lastname = new SimpleStringProperty(lastname);
		this.email = new SimpleStringProperty(email);
		this.filmTitle = new SimpleStringProperty(filmTitle);
		this.screenNum = new SimpleStringProperty(screenNum);
		this.date = new SimpleStringProperty(date.toString());
		this.time = new SimpleStringProperty(time);
		this.adultTickets = new SimpleIntegerProperty(adultTickets);
		this.childTickets = new SimpleIntegerProperty(childTickets);
		this.seniorTickets = new SimpleIntegerProperty(seniorTickets);
		this.totalPrice = new SimpleDoubleProperty(totalPrice);
		this.isVip = new SimpleBooleanProperty(isVip);
	}

	// Getters for all properties
	public int getId() {
		return id.get();
	}

	public String getFirstname() {
		return firstname.get();
	}

	public String getLastname() {
		return lastname.get();
	}

	public String getEmail() {
		return email.get();
	}

	public String getFilmTitle() {
		return filmTitle.get();
	}

	public String getScreenNum() {
		return screenNum.get();
	}

	public String getDate() {
		return date.get();
	}

	public String getTime() {
		return time.get();
	}

	public int getAdultTickets() {
		return adultTickets.get();
	}

	public int getChildTickets() {
		return childTickets.get();
	}

	public int getSeniorTickets() {
		return seniorTickets.get();
	}

	public double getTotalPrice() {
		return totalPrice.get();
	}

	public boolean getIsVip() {
		return isVip.get();
	}
}