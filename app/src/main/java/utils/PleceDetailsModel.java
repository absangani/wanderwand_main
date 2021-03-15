package utils;

public class PleceDetailsModel {
    private String Plece_name;
    private int Plece_rating;
    private int Plece_image;

    // Constructor
    public PleceDetailsModel(String Plece_name, int Plece_rating, int Plece_image) {
        this.Plece_name = Plece_name;
        this.Plece_rating = Plece_rating;
        this.Plece_image = Plece_image;
    }

    // Getter and Setter
    public String getPlece_name() {
        return Plece_name;
    }

    public void setPlece_name(String Plece_name) {
        this.Plece_name = Plece_name;
    }

    public int getPlece_rating() {
        return Plece_rating;
    }

    public void setPlece_rating(int Plece_rating) {
        this.Plece_rating = Plece_rating;
    }

    public int getPlece_image() {
        return Plece_image;
    }

    public void setPlece_image(int Plece_image) {
        this.Plece_image = Plece_image;
    }
}
