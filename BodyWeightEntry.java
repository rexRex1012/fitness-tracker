import java.time.LocalDate;

public class BodyWeightEntry {
  private LocalDate date;
  private double weight;    

public BodyWeightEntry(LocalDate date, double weight) {
    this.date = date;
    this.weight = weight;
}
public LocalDate getDate() {
    return date;
}
public double getWeight() {
    return weight;
}
@Override
public String toString() {
    return "Date: " + date + ", Weight: " + weight + " lbs";
    }
}
