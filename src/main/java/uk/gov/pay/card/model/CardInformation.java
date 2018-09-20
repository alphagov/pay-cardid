package uk.gov.pay.card.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.lang.String.format;

public class CardInformation {

    private String brand;
    private String type;
    private String label;
    private Long min;
    private Long max;
    private boolean corporate;

    private static Map<String, String> brandMapping;

    static {
        Map<String, String> brands = new HashMap<>();
        brands.put("MC", "master-card");
        brands.put("MCI DEBIT", "master-card");
        brands.put("MCI CREDIT", "master-card");
        brands.put("MAESTRO", "maestro");
        brands.put("AMERICAN EXPRESS", "american-express");
        brands.put("DINERS CLUB", "diners-club");
        brands.put("VISA CREDIT", "visa");
        brands.put("VISA DEBIT", "visa");
        brands.put("ELECTRON", "visa");
        brandMapping = Collections.unmodifiableMap(brands);
    }

    public CardInformation(String brand, String type, String label, Long min, Long max, boolean corporate) {
        this.brand = brand;
        this.type = type;
        this.label = label;
        this.min = min;
        this.max = max;
        this.corporate = corporate;
    }

    public CardInformation(String brand, String type, String label, Long min, Long max) {
        this(brand, type, label, min, max, false);
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

    public boolean isCorporate() {
        return corporate;
    }

    public void updateRangeLength(int numLength) {
        min = Long.valueOf(format("%-" + numLength + "d", min).replace(" ", "0"));
        max = Long.valueOf(format("%-" + numLength + "d", max).replace(" ", "9"));
    }

    public void transformBrand() {
        if (brandMapping.containsKey(brand)) {
            this.brand = brandMapping.get(brand);
        }
        this.brand = this.getBrand().toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardInformation that = (CardInformation) o;
        return corporate == that.corporate &&
                Objects.equals(brand, that.brand) &&
                Objects.equals(type, that.type) &&
                Objects.equals(label, that.label) &&
                Objects.equals(min, that.min) &&
                Objects.equals(max, that.max);
    }

    @Override
    public int hashCode() {
        return Objects.hash(brand, type, label, min, max, corporate);
    }

    @Override
    public String toString() {
        return "CardInformation{" +
                "brand='" + brand + '\'' +
                ", type='" + type + '\'' +
                ", label='" + label + '\'' +
                ", min=" + min +
                ", max=" + max +
                ", corporate=" + corporate +
                '}';
    }
}
