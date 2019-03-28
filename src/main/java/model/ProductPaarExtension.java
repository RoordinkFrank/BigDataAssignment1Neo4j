package model;

public class ProductPaarExtension extends ProductPaar implements Comparable<ProductPaarExtension>{
	public void sortKlantenAlphabetical(){
		int i = String.CASE_INSENSITIVE_ORDER.compare(firstProductKey, otherProductKey);
		if (i > 0){
			String tempProductKey = firstProductKey;
			firstProductKey = otherProductKey;
			otherProductKey = tempProductKey;
		}
	}

	@Override
	public int compareTo(ProductPaarExtension otherProductPaar) {
		if (otherProductPaar.aantal == aantal)
			return 0;
		else if (otherProductPaar.aantal > aantal)
			return 1;
		else if (otherProductPaar.aantal < aantal)
			return -1;
		return 0;
	}
}
