package org.spacedown.activity.db;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.spacedown.R;
import org.spacedown.SpacedownApp;
import org.spacedown.database.CardsDataSource;
import org.spacedown.engine.game.Card;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class ImportCardsInDBActivity extends Activity implements OnClickListener {

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
		setContentView(R.layout.activity_import_card_in_db);
		setTitle(R.string.create_card_title);

		datasource = new CardsDataSource(app.getApplicationContext());
		
		this.refreshActivity();

	}

	private void refreshActivity() {
		this.initTexts();
		this.initButtons();
	}

	private void initButtons() {
		// TODO need to read a CSV file
		buttonProcessRequest = (Button) findViewById(R.id.buttonConfirmFilterCard);
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
			List<Card> importHardCodedListToDB = importHardCodedListToDB(keepCustomCards.isChecked());
			if (importHardCodedListToDB != null && !importHardCodedListToDB.isEmpty()) {
				Toast toast = Toast.makeText(
						this,
						String.format(mResources.getString(R.string.dialog_import_cards_ok),
								importHardCodedListToDB.size()), Toast.LENGTH_LONG);
				toast.show();
			} else {
				Toast toast = Toast.makeText(this,
						String.format(mResources.getString(R.string.dialog_import_cards_ko)), Toast.LENGTH_LONG);
				toast.show();
			}
			this.refreshActivity();
		}
	}

	public List<Card> importHardCodedListToDB(boolean keepCustomCards) {

		// Backup custom cards
		List<Card> allCustomCards = null;
		if (keepCustomCards) {
			allCustomCards = datasource.getAllCustomCards();
		}

		List<Card> cardList = new ArrayList<Card>();
		String listNames = "Nutrition Seignalet,Facebook,Saison,France,Le Trône de fer (série télévisée),YouTube,Nelson Mandela,Google,Madonna,Terre,Wiki,Association sportive de Monaco football club,Liste des épisodes de Naruto Shippūden,Lune,The Walking Dead (série télévisée),Test,Victor Hugo,Paris,Andreas Wolf,Nacer Barazite,Gary Kagelmacher,Nabil Dirar,Stromae,Lionel Messi,Andrea Raggi,Lucas Ocampos,Breaking Bad,Flavio Roma,Jakob Poulsen,One Direction,Protocole de Kyoto,John Cena,États-Unis,Liste des indicatifs téléphoniques internationaux par indicatif,Harry Potter,Harlem shake,Pont suspendu,Cristiano Ronaldo,Courant continu,Pretty Little Liars,Mounir Obbadi,Frigide Barjot,Appareil,Domestique,Combustible fossile,AOSIS,Convention-cadre des Nations unies sur le changement climatique,IRENA,ALBA,Albert Camus,Gad Elmaleh,Eminem,Rihanna,Grace Hopper,Chat,Rayonnement solaire,Tal (chanteuse),Molière,Daft Punk,Adolf Hitler,Danijel Subašić,Sexion d'Assaut,Liste des épisodes de One Piece,Saison 9 de Grey's Anatomy,Maître Gims,Yióryos Tzabélas,GRB,Booba,Leonardo DiCaprio,Macklemore,New York,Grey's Anatomy,The Big Bang Theory,BB,Once Upon a Time (série télévisée),Ibrahima Touré,Louis XIV de France,Paul Walker,Première Guerre mondiale,Pablo Picasso,La Fouine,Claudio Ranieri,Selena Gomez,Seconde Guerre mondiale,Voltaire,Maroc,Doctor Who,G,HD,Quentin Tarantino,Canada,How I Met Your Mother,Enora Malagré,Emmanuel Rivière,Tour Eiffel,Michael Jackson,Trouble bipolaire,Dexter (série télévisée),Miley Cyrus,Homeland (série télévisée),Vampire Diaries,Abraham Lincoln,Jennifer Lawrence,Léonard de Vinci,Signe du Zodiaque,Saison 2012-2013 de l'AS Monaco FC,Ryan Gosling,Aléxandros Tziólis,Le Trône de fer,Johnny Depp,Gale,Martin Luther King,Revenge (série télévisée),Sebastián Ribas,Algérie,Emir Bajrami,Guy de Maupassant,Zlatan Ibrahimović,Belgique,Justin Bieber,Statue de la Liberté,Nicolas Copernic,Dieudonné,Allemagne,Suisse,Cinquante nuances de Grey,Shakira,Glee (série télévisée),Australie,Les Frères Scott,Espagne,Schizophrénie,Jenifer,Ludovic Giuly,Charles Baudelaire,Jason Statham,Département français,Albert Einstein,Fichier .PAC,Format de papier,Jean-Jacques Goldman,Patrick Bruel,Kim Kardashian,Shy'm,Les Simpson,Jean de La Fontaine,Neymar,Dwayne Johnson,Fairy Tail,Carl Medjani,Christophe Colomb,Brad Pitt,Angelina Jolie,James Bond,Napoléon Ier,Brésil,M. Pokora,Ricardo Carvalho,Saison 2013-2014 de l'AS Monaco FC,Mentalist,Marilyn Monroe,Union européenne,Dr House,Titanic,Liste des épisodes de Fairy Tail,Solstice,Saison 8 de How I Met Your Mother,Wikipédia:Avenue des cafés et bistros,Liste des indicatifs téléphoniques internationaux par pays,Italie,Sept merveilles du monde,Alizée,Taille du pénis chez l'homme,Mila Kunis,Japon,Cyril Hanouna,Liste des départements français,Scarlett Johansson,Arrow (série télévisée),Maladie de Crohn,Desperate Housewives,Édith Piaf,Vitamine D,Gérard Depardieu,Bruno Mars,Aide:Premiers pas,Naruto,Nicolas Isimat-Mirin,Russie,Wolfgang Amadeus Mozart,Bob Marley,Tupac Shakur,Saison 3 de The Walking Dead,Sons of Anarchy,Rafael Nadal,Batman,Royaume-Uni,One Piece,Barcelone,Johnny Hallyday,Système solaire,Ma famille d'abord,Downton Abbey,La Joconde,Franc-maçonnerie,Valérie Benguigui,Turquie,Bruce Willis,Guernica (Picasso),Inde,Emma Watson,Internet,Alain Soral,Margaret Thatcher,Taylor Swift,Nicki Minaj,Saison 3 du Trône de fer,Charlie Chaplin,Jamel Debbouze,Saison 2 de Once Upon a Time,Salvador Dalí,Jules César,Masse volumique,Saison 4 de Glee,Tunisie,Londres,Barack Obama,Élisabeth II du Royaume-Uni,Carpe diem,Ligue des champions de l'UEFA,Céline Dion,Portugal,Islam,Tableau périodique des éléments,Chine,Bruce Lee,Qatar,Émile Zola,Halloween,Sylvester Stallone,Twilight,Afrique du Sud,Android,Pointures et tailles en habillement,Django Unchained,Moyen Âge,Français,Claude Bernard,Guadeloupe,Jean Dujardin,Spartacus,Beyoncé Knowles,Bradley Cooper,John Fitzgerald Kennedy,David Beckham,Le Seigneur des anneaux,Mali,Charmed,Brigitte Bardot,William Shakespeare,Aide:Homonymie,Lyon,Gossip Girl (série télévisée),Toulouse,Nicolas Sarkozy,Liste des pays du monde,Île Maurice,Natalie Portman,Cheval,Singapour,Cigarette électronique,Virginie Efira,Jean-Jacques Rousseau,Will Smith,Arthur Rimbaud,Europe,Claude François,Che Guevara,Megan Fox,Esprits criminels,Charles de Gaulle,Tuberculose,Jérôme Cahuzac,Affaire de Roswell,Coupe du monde de football de 2014,Clara Morgane,Louis de Funès,Serge Gainsbourg,C (langage),La Réunion,Tom Cruise,Zona,Andy Warhol,Saison 5 de Breaking Bad,Vin Diesel,Paris Saint-Germain Football Club,Saison 4 de Vampire Diaries,Emilia Clarke,Malcolm (série télévisée),La Petite Maison dans la prairie,Québec,Fibromyalgie,Apartheid,Pays-Bas,Harry Potter (films),Claude Lévi-Strauss,Channing Tatum,Les Misérables,Mohamed Ali (boxe anglaise),Star Wars,L'Attaque des Titans,Révolution française,Marion Cotillard,Cœur,Profilage (série télévisée),XXe siècle,Under the Dome (série télévisée),Alexandre le Grand,Liste des capitales du monde,Madagascar,Marseille,Développement durable,Roger Federer,Arnold Schwarzenegger,Romantisme,Hunger Games,Indicatif téléphonique local en France,Sclérose en plaques,Cap-Vert,Henri IV de France,Lion,Aide:Importer un fichier,Franck Ribéry,Ronaldinho,Saint-Valentin,Alain Delon,Twitter,Pâques,Adele,The Beatles,Afrique,Accident vasculaire cérébral,Masturbation,Person of Interest,Martinique,Jean-Paul Belmondo,Syndrome d'Asperger,Ludwig van Beethoven,Mur de Berlin,Benoît XVI,Katy Perry,Fête des Mères,Monica Bellucci,FC Barcelone (football),Friedrich Nietzsche,Rome,Steven Spielberg,Angleterre,Apple,Sophie Marceau,Alphabet grec,Lady Gaga,Anonymous (collectif),90210 Beverly Hills : Nouvelle Génération,Raymond Loewy,Wikipédia:Contact,Jean-Paul II,Alfred Hitchcock,Croatie,La Marseillaise,Britney Spears,Najat Vallaud-Belkacem,Steve Jobs,Mohandas Karamchand Gandhi,Plus belle la vie,Attaque par déni de service,Irlande (pays),Charlemagne,Arobase,Cuba,Cory Monteith,Fringe,NCIS : Enquêtes spéciales,Violetta (série télévisée),Spartacus (série télévisée),Guerre froide,Syndrome d'immunodéficience acquise,Galerie des drapeaux des pays du monde,Lil Wayne,Kaamelott,Blake Lively,Saga Nouveau Monde de One Piece,Jeanne d'Arc,Liste de couleurs,Région française,American Horror Story,Christiane Taubira,Ku Klux Klan,Soleil,Sionisme,Jacques Brel,Tim Burton,Clint Eastwood,Liste des épisodes de Doctor Who,Zinédine Zidane,Castle (série télévisée),Saison 8 de Grey's Anatomy,Kaley Cuoco,Mexique,2013,Hermann Rorschach,Nouvelle-Calédonie,Karine Le Marchand,Robert Downey Jr.,Fête du Travail,Geek,Corée du Nord,Kev Adams,Léa Seydoux,Jacques Chirac,Ballon d'or,Justin Timberlake,Lost : Les Disparus,Supernatural (série télévisée),Burkina Faso,Maladie de Lyme,François Hollande,Candide,Pharrell Williams,Delvin Ndinga,Bernadette Lafont,Nina Dobrev,François Ier de France,Michael Jordan,Méningite,Illuminati,Ian Somerhalder,Compagnie de Jésus,Sergio Romero,Éliminatoires de la coupe du monde de football 2014 : zone Afrique,Elvis Presley,Attentats du 11 septembre 2001,Igor et Grichka Bogdanoff,Pascal Obispo,Fast and Furious 6,IPhone,Côte d'Ivoire,Discussion:Cocotier du Chili,Bordeaux,Vanessa Paradis,Liste des présidents de la République française,Goutte (maladie),Chien,Thaïlande,Vincent van Gogh,W,Heath Ledger,Otto Dix,Argentine,Wikipédia:Accueil de la communauté,Gastro-entérite,Îles Canaries,João Moutinho,Saison 5 de Mentalist,Aide:Référence nécessaire,Claude Monet,Anthony Martial,Pollution,Anémie,Joyce Jonathan,Robert De Niro,Bleach (manga),Liste des communes de France les plus peuplées,Charlotte Casiraghi,Friends,Organisation des Nations unies,Mythologie grecque,Demi Lovato,Natacha Polony,Joseph Staline,Guerre de Sécession,Procrastination,Jennifer Love Hewitt,Nicolas Cage,Maria Callas,George Clooney,Nombre d'or,Lucy Hale,Euro,Monaco,Walt Disney,Gestion des ressources humaines,Maladie d'Alzheimer,Airbus A380,Liste détaillée des papes,Bernard Tapie,Mark Zuckerberg,Égypte,David Guetta,Vikings,Carla Bruni-Sarkozy,Hugh Jackman,Coca-Cola,Honoré de Balzac,Isabelle Adjani,Informatique,Teen Wolf (série télévisée),Pornographie,Liste des diplômes en France,Prison Break,Ostéopathie,Mayotte,Malaise vagal,Ben Affleck,Real Madrid Club de Fútbol,Jennifer Lopez,Denzel Washington,Pape,Les Schtroumpfs,Coluche,Istanbul,Alicia Keys,Psoriasis,Rohff,Recherche,Frida Kahlo,Scènes de ménages,Anne Hathaway (actrice),True Blood,Coupe d'Afrique des nations de football 2013,Randy Orton,Montréal,Intouchables (film),Nagui,Personnages de The Walking Dead,Équipe de France de football,Titanic (film, 1997),Lana Del Rey,Israël,Les Anges de la téléréalité,Corse,Catherine Deneuve,Dragon Ball Z,Mahomet,Jean-Claude Van Damme,Charles Aznavour,Smartphone,Nouvelle-Zélande,Football,Liste des présidents des États-Unis,Film pornographique,Alexandre Dumas,Thomas Bangalter,Manuel Valls,Islande,Soda (série télévisée),Guillaume Canet,Cloud computing,Emma Stone,Lena Headey,Liste des monarques de France,Prostate,Communication,Amour,Jackie Chan,Paludisme,Marrakech,Sodomie,Produit intérieur brut,Autisme,Pierre Corneille,France Gall,Sherlock Holmes,Salma Hayek,WWE,Cathédrale Notre-Dame de Paris,Saison 8 de Dexter,James Franco,Pirates des Caraïbes,Arthrose,Malte,Matt Damon,Amber Heard,Philippines,Pink Floyd,Zaz (chanteuse),Aristote,Lupus érythémateux disséminé,Geoffrey Kondogbia,Paul Pogba,Grèce,Noël,Garou,Kristen Stewart,Ashley Benson,Personnages du Trône de fer,Los Angeles,Hawaii 5-0 (série télévisée, 2010),Spider-Man,Noémie Lenoir,Les Feux de l'amour,Linux,Liban,Rythme cardiaque,Mylène Farmer,Stephen King,Suède,Jean Gabin,Entreprise,Syrie,PlayStation 3,Empathie,Impressionnisme,Cookie (informatique),Serge Ayoub,Bali,Robin Thicke,Shoah,Marie-Antoinette d'Autriche,Guerre du Viêt Nam,Antoni Gaudí,Fast and Furious,Pologne,Tony Parker,République dominicaine,Bill Gates,Ordinateur,Affaire Florence Cassez,Wikipédia,Philosophie,Liam Hemsworth,Hugo Chávez,Claire Chazal,Hypertext Transfer Protocol,Embolie pulmonaire,Al Pacino,Saints de glace,Saison 2 de Revenge,Ligue des champions de l'UEFA 2013-2014,Hipster,Internet Movie Database,Sandra Bullock,Jake Gyllenhaal,Nantes,Antoine de Caunes,Ulysse,Stéphane Hessel,Radamel Falcao García,Éliminatoires de la Coupe du monde de football 2014,Syphilis,Michaël Youn,Gareth Bale,Snoop Dogg,Alessandra Sublet,Boris Vian,Roumanie,Louis XVI de France,Puissance d'un nombre,Mesut Özil,Valérie Bénaïm,Chloë Moretz,Nicholas Hoult,Basket-ball,Pentecôte,Chuck Norris,Danemark,Jésus de Nazareth,Julia Roberts,Zeus,Clitoris,Or,Keen'v,Omar Sy,Sp,Écart type,Affaire Dreyfus,Marie Curie,Coco Chanel,Île-de-France,Coupe du monde de football,Tom Hanks,Mark Wahlberg,Volcan,MDMA,Alexandra Lamy,Wi-Fi,Roms,Penélope Cruz,Fellation,Keanu Reeves,Sophie Davant,Samuel Eto'o,X-Men,Manga,Ashton Kutcher,Romy Schneider,Jim Carrey,Sophia Bush,Jean-Paul Sartre,Matrix,Liste des milliardaires du monde,Dubaï (ville),Saison 8 de Dr House,Jennifer Aniston,Guyane,Thierry Henry,Woody Allen,Diana Spencer,Angine,Daniel Craig,Little Mix,Orelsan,Appendicite,Grippe,Bicarbonate de sodium,Haïti,Jules Verne,Inception,Pyramide des besoins de Maslow,La Liberté guidant le peuple,Henri VIII d'Angleterre,Viêt Nam,République démocratique du Congo,Skyfall,Bretagne,Femen,Saison 7 de Dexter,François Mitterrand,Chris Brown,David Bowie,Pink (chanteuse),Karl Marx,Saison 6 de The Big Bang Theory,Rosa Parks,Siècle des Lumières,Danse avec les stars,Sexe,Venise,Roméo et Juliette,Ukraine,Luxembourg (pays),Sénégal,Ligue des champions de l'UEFA 2012-2013,Keira Knightley,Ronaldo Luis Nazário de Lima,50 Cent,Mariage,Papillomavirus humain,Eau,Écosse,Vladimir Poutine,Taylor Lautner,Steve McQueen,Star Trek,Vanessa Hudgens,Freddie Mercury,American Pie (film),Saison 3 de Pretty Little Liars,JoeyStarr,Liste des papes,Théâtre,Décès en 2013,Charlize Theron,Surréalisme,Liste des épisodes de Bleach,Cameron Diaz,Mike Tyson,Renaud,Jimi Hendrix,Galilée (savant),Daniel Day-Lewis,Jean-Michel Maire,Asaf Avidan,Birmanie,Iran,Muse (groupe),Chris Hemsworth,Rachel McAdams,Vulve,Aluminium,Superman,Leïla Bekhti,Florent Pagny,Bouddhisme,Théorème de Pythagore,Youssoupha,Liam Neeson,Berlin,Christoph Waltz,Cameroun,Leucocyte,Louis Bertignac,Maladie de Parkinson,Morgan Freeman,Avengers (film),Les Tortues ninja,Joseph Gordon-Levitt,Cut Killer,Tomate,Christian Bale,Chandeleur,Bryan Cranston,Olivia Wilde,Ariana Grande,Guillaume Apollinaire,François (pape),Guerre d'Algérie,Tahiti,Management,Groupe sanguin,Spasmophilie,Gwyneth Paltrow,Marketing,Dinosaure,Touche pas à mon poste !,Bourvil,Martina Stoessel,Varicelle,Guerre civile syrienne,Cunnilingus,Vagin,Indonésie,Maldives,Championnat du monde de handball masculin 2013,Portail:Musique,Émoticône,Vikings (série télévisée),Lymphome,Emily VanCamp,Platon,Montpellier,Pont,Skins (série télévisée, 2007),Utilisateur:Pouark/Livres/Interests,Basile de Koch,Autriche,Karim Benzema,Gangnam Style,Jared Leto,Wikipédia:Mois de la contribution,Liste de tueurs en série,Lou Doillon,Théories du complot Illuminati,Michel Drucker,Éric Abidal,Chocolat,Maurice Sendak,C2C (groupe),Daniel Radcliffe,Diabète,Andorre,Amsterdam,Olympique de Marseille,Sex and the City,Bitcoin,Birdy (chanteuse),SWOT,Google Maps,Kurt Cobain,Dassault Rafale,Leonhard Euler,Smallville,Lara Fabian,Tour de France (cyclisme),Avril Lavigne,Le Hobbit,Jodie Foster,Arielle Dombasle,André Le Nôtre,Fibre optique,Arthur (animateur),Serena Williams,Brahim Zaibat,Octet,Joel et Ethan Coen,Dépression (psychiatrie),Sinusite,Sigmund Freud,Coqueluche,Rap,Mariage homosexuel,Cinéma,Georges Moustaki,Montesquieu,Prague,Stephen Hawking,Hong Kong,Vincent Cassel,Georges Clemenceau,Audrey Hepburn,Tigre,Woodkid,John Lennon,Acide désoxyribonucléique,Laury Thilleman,Alexandre Astier,Mary-Kate et Ashley Olsen,Jay-Z,Zac Efron,Viaduc de Millau,The Undertaker,Guerre de Cent Ans,Antigone (Anouilh),Proxy,Bones,Kate Winslet,Scandal (série télévisée),Hypothyroïdie,Saison 5 de Castle,Denis Diderot,Nicole Kidman,XIXe siècle,Adèle Exarchopoulos,Paradigme,Michel-Ange,Classicisme,Tom Hardy,Winston Churchill,Strasbourg,Herpès,Roi Arthur,Louis Pasteur,Syndrome du canal carpien,Lille,Arabie saoudite,Louis XV de France,Hannah Arendt,Foie,Psy (chanteur),Maria Sharapova,Sagrada Família,Madrid,Nicolas Bedos,Costa Rica,Audrey Fleurot,Acier,Pénis,Minecraft,Isaac Newton,Rapport sexuel,Révolution industrielle,Wolverine,Eva Mendes,Nolwenn Leroy,Adriana Karembeu,George Sand,Guy-Manuel de Homem-Christo,Pokémon,Diane Kruger,Whitney Houston,IAM,Fais pas ci, fais pas ça,Oxymore,Domotique,Famille Borgia,Mononucléose infectieuse,Marie Drucker,Malaisie,Dyslexie,James Rodríguez,Finlande,Drapeau de la France,Hunger Games (film),Spring Breakers,Skype,Chili,Liste des pays et territoires par superficie,Google (moteur de recherche),House of Cards (série télévisée, 2013),Kaká,Daphné Bürki,Paul Verlaine,Théorème de Thalès,Lea Michele,Énergie renouvelable,Mouton,American Wives,France d'outre-mer,Kanye West,Belote,Nigeria,Man of Steel,Bugatti Veyron 16.4,Hayao Miyazaki,Les Mystères de l'amour,Napoléon III,Victoria du Royaume-Uni,Guitare,Rouen,Badr Hari,NCIS : Los Angeles,Système d'exploitation,Roman Polanski,CM Punk,Hernie discale,Ellen Page,Queen";

		StringTokenizer st = new StringTokenizer(listNames, ",");

		while (st.hasMoreTokens()) {
			Card newCard = new Card();
			newCard.setNameToFind(st.nextToken());
			cardList.add(newCard);
		}

		if (cardList != null && !cardList.isEmpty()) {
			// Flush current table
//			datasource.dropCardTable(); TODO 

			cardList = datasource.addCards(cardList);
		}

		// Put back custom cards in DB
		if (allCustomCards != null && !allCustomCards.isEmpty()) {
			cardList.addAll(datasource.addCards(allCustomCards));
		}
		return cardList;
	}

}
