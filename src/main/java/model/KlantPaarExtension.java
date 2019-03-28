package model;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.Klant;
import model.KlantPaar;


public class KlantPaarExtension extends KlantPaar{
	public void sortKlantenAlphabetical(){
		int i = String.CASE_INSENSITIVE_ORDER.compare(firstKlant.key, otherKlant.key);
		if (i > 0){
			Klant tempKlant = firstKlant;
			firstKlant = otherKlant;
			otherKlant = tempKlant;
		}
	}
}
