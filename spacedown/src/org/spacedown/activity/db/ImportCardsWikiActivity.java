package org.spacedown.activity.db;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONObject;
import org.spacedown.R;
import org.spacedown.SpacedownApp;
import org.spacedown.database.CardsDataSource;
import org.spacedown.engine.game.Card;

import android.app.Activity;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class ImportCardsWikiActivity extends Activity implements OnClickListener {

	private static final String TAG = ImportCardsWikiActivity.class.getSimpleName();

	/**
	 * Reference to Application object
	 */
	private SpacedownApp app;

	private Resources mResources;

	private Button buttonProcessRequest;

	private TextView numberOfActiveCards;

	private TextView numberOfInactiveCards;

	private CheckBox keepCustomCards;

	private CardsDataSource datasource;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// reference to application object
		app = ((SpacedownApp) getApplication());

		mResources = getResources();
		setContentView(R.layout.activity_import_card_in_db_wiki);
		setTitle(R.string.import_cards_wiki);

		datasource = new CardsDataSource(app.getApplicationContext());

		this.refreshActivity();

	}

	private void refreshActivity() {
		// this.initTexts();
		this.initButtons();
	}

	private void initButtons() {
		// TODO need to read a CSV file
		buttonProcessRequest = (Button) findViewById(R.id.buttonImportWiki);
		buttonProcessRequest.setOnClickListener(this);

		keepCustomCards = (CheckBox) findViewById(R.id.checkBoxKeepCustomCards);
		keepCustomCards.setOnClickListener(this);
	}

	private void initTexts() {
		numberOfActiveCards = (TextView) findViewById(R.id.textFilterDbDisplayActiveCards);
		numberOfInactiveCards = (TextView) findViewById(R.id.textFilterDbDisplayInactiveCards);

		List<Card> listActiveCards = datasource.getAllCards(true);
		if (listActiveCards != null && !listActiveCards.isEmpty()) {
			numberOfActiveCards.setText(String.format(mResources.getString(R.string.db_number_cards),
					listActiveCards.size()));
		} else {
			numberOfActiveCards.setText(String.format(mResources.getString(R.string.db_no_active_card)));
		}

		List<Card> listInactiveCards = datasource.getAllCards(false);
		if (listInactiveCards != null && !listInactiveCards.isEmpty()) {
			numberOfInactiveCards.setText(String.format(mResources.getString(R.string.db_number_cards_inactive),
					listInactiveCards.size()));
		} else {
			numberOfInactiveCards.setText(String.format(mResources.getString(R.string.db_no_inactive_card)));
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == buttonProcessRequest.getId()) {
			// List<Card> importedCards = importCardsFromWikimedia();
			// importHardCodedListToDB();

			new LongOperation().execute("");

			//
			// if (importedCards != null && !importedCards.isEmpty()) {
			// // addImportedCardsToDB(importedCards,
			// // keepCustomCards.isChecked());
			// Toast toast = Toast.makeText(this,
			// String.format(mResources.getString(R.string.dialog_import_cards_ok),
			// importedCards.size()),
			// Toast.LENGTH_LONG);
			// toast.show();
			// } else {
			// Toast toast = Toast.makeText(this,
			// String.format(mResources.getString(R.string.dialog_import_cards_ko)),
			// Toast.LENGTH_LONG);
			// toast.show();
			// }
			// this.refreshActivity();
		}
	}

	private class LongOperation extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String baseURI = "https://en.wikipedia.org/w/api.php?";
			String actionQuery = "action=query";
			String AND = "&";
			String prop = "prop=";
			String propRevisions = "revisions";
			String propLinks = "links";
			String titles = "titles=";
			String titleMJ = "Michael%20Jackson";
			String titleListFrenchPeople = "List_of_French_people";
			String rvprop = "rvprop=content";
			String format = "format=json";


			String continueStr = "continue=";
			String list = "list=categorymembers";
			String catTitle = "cmtitle=Category:Lists_of_people";

			
			//Caller: importCardsFromWikimedia, URI: https://en.wikipedia.org/w/api.php?action=query&titles=List_of_French_people&format=json&prop=links&rvprop=content

//			String URIPageFrench = baseURI + actionQuery + AND + titles + titleListFrenchPeople + AND + continueStr + AND + format + AND + prop + propLinks + AND + rvprop;
			String URIPageFrench = "https://en.wikipedia.org/w/api.php?action=query&titles=List_of_French_people&format=json&prop=links&rvprop=content";
			String URIPageMJ = "https://en.wikipedia.org/w/api.php?action=query&titles=Michael%20Jackson&format=json&prop=revisions&rvprop=content";
			String URIPagePerCat = baseURI + actionQuery + AND + list + AND + continueStr + AND + format + AND + catTitle;
			String fetch = "";

			
			
//			https://en.wikipedia.org/wiki/List_of_French_people
			
			WikiConnection wiki = new WikiConnection();

			try {
				fetch = wiki.fetch(URIPageMJ, "importCardsFromWikimedia");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return fetch;
		}

		@Override
		protected void onPostExecute(String result) {
//			decodeWikiJSONCategories(result);
			decodeWikiJSONListFrench(result);
		}

		private void decodeWikiJSONListFrench(String result) {
			
			
			/*{"query":{
			 * 		"normalized":[
			 * 			{"to":"List of French people","from":"List_of_French_people"}
			 * 		],
			 * 		"pages":{"10678":{"revisions":[{"*":"{{BLP sources|list|date=July 2013}}\n{{Use dmy dates|date=May 2015}}\n{{dynamic list|date=July 2013}}\n'''[[French people]]''' of note include:\n{{TOC right}}
			 * 
			 * 		\n\n==Actors==\n{{Main|List of French actors}}
			 * 		\n\n===A&ndash;C===\n{{div col|colwidth=30em}}
			 * 		\n*[[Isabelle Adjani]]
			 * 		\n*[[Renée Adorée]]
			 * 		\n*[[Anouk Aimée]]
			 * 		\n*[[Flo Ankah]]\n*[[Arletty]]\n*[[Antonin Artaud]]\n*[[Fanny Ardant]]\n*[[Jeanne Aubert]]\n*[[Jean-Louis Aubert]]\n*[[Jean-Pierre Aumont]]\n*[[Claude Autant-Lara]]\n*[[Daniel Auteuil]]\n*[[Charles Aznavour]]\n*[[Brigitte Bardot]]\n*[[Emmanuelle Béart]]\n*[[Jean-Paul Belmondo]]\n*[[François Berléand]]\n*[[Charles Berling]]\n*[[Suzanne Bianchetti]]\n*[[Juliette Binoche]]\n*[[Bernard Blier]]\n*[[Sandrine Bonnaire]]\n*[[Élodie Bouchez]]\n*[[Bourvil]]\n*[[Dany Boon]]\n*[[Angelique Boyer]]\n*[[Charles Boyer]]\n*[[Guillaume Canet]]\n*[[Capucine]]\n*[[Martine Carol]]\n*[[Leslie Caron]]\n*[[Isabelle Carré]]\n*[[Vincent Cassel]]\n*[[Jean-Pierre Cassel]]\n*[[Laetitia Casta]]\n*[[Robert Clary]]\n*[[Grégoire Colin]]\n*[[Marion Cotillard]]\n*[[Clotilde Courau]]\n*[[Darry Cowl]]\n{{div col end}}\n\n===D–L===\n{{div col|colwidth=30em}}\n*[[Béatrice Dalle]]\n*[[Lili Damita]]\n*[[Danielle Darrieux]]\n*[[Alain Delon]]\n*[[Danièle Delorme]]\n*[[Julie Delpy]]\n*[[Catherine Deneuve]]\n*[[Élisabeth Depardieu]]\n*[[Gérard Depardieu]]\n*[[Guillaume Depardieu]]\n*[[Patrick Dewaere]]\n*[[Arielle Dombasle]]\n*[[Michel Drucker]]\n*[[Morgane Dubled]]\n*[[Jean Dujardin]]\n*[[Anny Dupérey]]\n*[[Romain Duris]]\n*[[Nicolas Duvauchelle]]\n*[[Fernandel]]\n*[[Brigitte Fossey]]\n*[[Louis de Funès]]\n*[[Félicité Du Jeu]]\n*[[Jean Gabin]]\n*[[Julie Gayet]]\n*[[Annie Girardot]]\n*[[Judith Godrèche]]\n*[[Eva Green]]\n*[[Sacha Guitry]]\n*[[Isabelle Huppert]]\n*[[Irène Jacob]]\n*[[Claude Jade]]\n*[[Marlène Jobert]]\n*[[Valérie Kaprisky]]\n*[[Mélanie Laurent]]\n*[[Jean-Pierre Léaud]]\n*[[Virginie Ledoyen]]\n*[[Noémie Lenoir]]\n*[[Max Linder]]\n*[[Sheryfa Luna]]\n{{div col end}}\n\n===M–Z===\n{{div col|colwidth=30em}}\n*[[Marcel Marceau]]\n*[[Sophie Marceau]]\n*[[Jean Marais]]\n*[[Olivier Martinez]]\n*[[Jean-Baptiste Maunier]]\n*[[Miou-Miou]]\n*[[Mistinguett]]\n*[[Yves Montand]]\n*[[Jeanne Moreau]]\n*[[Michèle Morgan]]\n*[[Musidora]]\n*[[Gérard Philipe]]\n*[[Michel Piccoli]]\n*[[Clémence Poésy]]\n*[[Alexia Portal]]\n*[[Yvonne Printemps]]\n*[[Pérette Pradier]]\n*[[Jérôme Pradon]]\n*[[Rachel (actress)]] ''pseudonym for [[Elisa-Rachel Félix]]\n*[[Gabrielle Réjane]]\n*[[Jean Reno]]\n*[[Marine Renoir]]\n*[[Pierre Richard]]\n*[[Sebastian Roché]]\n*[[Jean Rochefort]]\n*[[Béatrice Romand]]\n*[[Philippine de Rothschild]]\n*[[Nathalie Roussel]]\n*[[Michel Roux (actor)|Michel Roux]]\n*[[Emmanuelle Seigner]]\n*[[Delphine Seyrig]]\n*[[Simone Signoret]]\n*[[Audrey Tautou]]\n*[[Jean-Louis Trintignant]]\n*[[Marie Trintignant]]\n*[[Gaspard Ulliel]]\n*[[Michael Vartan]]\n*[[Hervé Villechaize]]\n*[[Lambert Wilson]]\n{{div col end}}\n\n==Architects==\n{{main|List of French architects}}\n{{div col|colwidth=30em}}\n*[[Jacques-François Blondel]]\n*[[Germain Boffrand]]\n*[[Étienne-Louis Boullée]]\n*[[Salomon de Brosse]]\n*[[Libéral Bruant]]\n*[[Androuet du Cerceau|Androuet du Cerceau family]]\n*[[Le Corbusier]] ''pseudonym for [[Charles Edouard Jeanneret]]'' (Swiss-born)\n*[[Philibert de l'Orme]]\n*[[Gustave Eiffel]]\n*[[Pierre François Léonard Fontaine]]\n*[[Ange-Jacques Gabriel]]\n*[[Charles Garnier (architect)|Charles Garnier]]\n*[[Tony Garnier (architect)|Tony Garnier]]\n*[[Hector Guimard]]\n*[[Villard de Honnecourt]]\n*[[Pierre Jeanneret]] (Swiss-born)\n*[[Henri Labrouste]]\n*[[Claude Nicolas Ledoux]]\n*[[Pierre Lescot]]\n*[[André Lurçat]]\n*[[Robert Mallet-Stevens]]\n*[[François Mansart]]\n*[[Jules Hardouin Mansart]]\n*[[Louis Métezeau]]\n*[[Jean Nouvel]]\n*[[Charles Percier]]\n*[[Claude Perrault]]\n*[[Dominique Perrault]]\n*[[Auguste Perret]]\n*[[Christian de Por
			*/

			StringBuilder text = new StringBuilder();
			try {
				JSONObject json = new JSONObject(result);
				Log.v(TAG, json.toString());

				JSONObject jsonQuery = json.getJSONObject("query");
				
				 JSONArray jsonArray = jsonQuery.getJSONArray("categorymembers");
				 
				 for (int i = 0 ; i < jsonArray.length(); i++) {
					 JSONObject categoryMember = (JSONObject)jsonArray.get(i);
					 String memberTitle =  (String)categoryMember.get("title");
					 text.append(memberTitle);
					 text.append("\n");
					
				}
				 

			} catch (Exception e) {
				e.printStackTrace();
			}

			TextView txt = (TextView) findViewById(R.id.wikiPageContent);
			txt.setText(text);

		}

		private void decodeWikiJSONCategories(String result) {
			
			
			/*{"query":
			 * 	{"categorymembers":[
			 * 		{"title":"Lists of people","ns":0,"pageid":3883},
			 * 		{"title":"List of people in alternative medicine","ns":0,"pageid":624365},
			 * 		{"title":"List of Charvet customers","ns":0,"pageid":20014257},
			 * 		{"title":"List of people called by the Colombian Supreme court in the parapolitics scandal","ns":0,"pageid":11017180},
			 * 		{"title":"List of Dalits","ns":0,"pageid":19788151},{"title":"List of Design Indaba speakers","ns":0,"pageid":35817326},
			 * 		{"title":"List of people with Erdős-Bacon numbers","ns":0,"pageid":17476893},
			 * 		{"title":"List of EU people","ns":0,"pageid":268202},
			 * 		{"title":"List of people under Five Eyes surveillance","ns":0,"pageid":41677199},
			 * 		{"title":"List of people involved in the Fort Lee lane closure scandal","ns":0,"pageid":44329046}
			 * 					]
			 * 	},
			 * "continue":{"cmcontinue":"page|4755414e54414e414d4f204c495354|43344020","continue":"-||"},"batchcomplete":""}
			*/

			StringBuilder text = new StringBuilder();
			try {
				JSONObject json = new JSONObject(result);
				Log.v(TAG, json.toString());

				JSONObject jsonQuery = json.getJSONObject("query");
				
				 JSONArray jsonArray = jsonQuery.getJSONArray("categorymembers");
				 
				 for (int i = 0 ; i < jsonArray.length(); i++) {
					 JSONObject categoryMember = (JSONObject)jsonArray.get(i);
					 String memberTitle =  (String)categoryMember.get("title");
					 text.append(memberTitle);
					 text.append("\n");
					
				}
				 

			} catch (Exception e) {
				e.printStackTrace();
			}

			TextView txt = (TextView) findViewById(R.id.wikiPageContent);
			txt.setText(text);

		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

	// /**
	// * Get DB cards from wikimedia URI.
	// *
	// * @return
	// */
	// private List<Card> importCardsFromWikimedia() {
	// List<Card> cardList = new ArrayList<Card>();
	//
	// WikiConnection connection = new WikiConnection();
	// connection.execute("");
	//
	// // new WikiConnection()
	// // {
	// // @Override public void onPostExecute(String result)
	// // {
	// // TextView txt = (TextView) findViewById(R.id.output);
	// // txt.setText(result);
	// // }
	// // }.execute("");
	//
	// //
	//
	// //
	// // String URI =
	// //
	// "https://en.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content&format=json&titles=Michael%20Jackson";
	// //
	// // try {
	// // String fetch = fetch(URI, "importCardsFromWikimedia");
	// // } catch (IOException e) {
	// // // TODO Auto-generated catch block
	// // e.printStackTrace();
	// // }
	//
	// return cardList;
	// }

}
