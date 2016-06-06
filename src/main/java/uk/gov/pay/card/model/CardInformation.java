package uk.gov.pay.card.model;

public class CardInformation {

    private String brand;
    private String type;
    private String label;
    private Long min;
    private Long max;

    public CardInformation(String brand, String type, String label, Long min, Long max) {
        this.brand = brand;
        this.type = type;
        this.label = label;
        this.min = min;
        this.max = max;
    }

    public String getBrand() {
        return brand;
    }

    public String getType() {
        return type;
    }

    public String getLabel() {
        return label;
    }

    public Long getMax() {
        return max;
    }

    public Long getMin() {
        return min;
    }

    public void updateRangeLength(int numLength) {
        min = Long.valueOf(String.format("%-" + numLength +"d", min).replace(" ", "0"));
        max = Long.valueOf(String.format("%-" + numLength + "d", max).replace(" ", "0"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CardInformation that = (CardInformation) o;

        if (brand != null ? !brand.equals(that.brand) : that.brand != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (label != null ? !label.equals(that.label) : that.label != null) return false;
        if (min != null ? !min.equals(that.min) : that.min != null) return false;
        return max != null ? max.equals(that.max) : that.max == null;

    }

    @Override
    public int hashCode() {
        int result = brand != null ? brand.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (min != null ? min.hashCode() : 0);
        result = 31 * result + (max != null ? max.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CardInformation{" +
                "brand='" + brand + '\'' +
                ", type='" + type + '\'' +
                ", label='" + label + '\'' +
                ", min=" + min +
                ", max=" + max +
                '}';
    }
}
