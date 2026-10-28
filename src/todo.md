# pathparam vs querystring
- https://www.linkedin.com/posts/ajith-a-2b3779316_webdevelopment-api-nodejs-activity-7350852766296174595-976z

Spørg chat hvorfor man ikke bare henter value direkte fra json key:

String json = apiReader.getVehicle(registrationNumber);
VehicleDTO vehicleDTO = apiReader.convertFromJson(json);

Du konverterer til en DTO (Data Transfer Object) i stedet for at læse nøglerne manuelt, fordi det gør din kode struktureret, nem at vedligeholde og type-sikker.
Her er de vigtigste grunde til, at en DTO er den bedste løsning:

1. Kompileringsfejl i stedet for Runtime-fejl (Type-sikkerhed)

Hvis du udtrækker værdier manuelt (f.eks. json.get("speed")), arbejder du typisk med tekststrenge. Hvis du staver forkert til en nøgle, opdager du det først, når koden kører, og applikationen genstarter eller går ned.
• Med en DTO fanger din compiler fejlen med det samme. Hvis du skriver vehicleDTO.getSped(), brokker dit IDE sig med en rød streg, før du overhovedet kører koden.


# læs op på app.dao

# 3. Hvordan ville du sikre, at begge gemmes — eller ingen af dem?

Her mangler den vigtigste del af dit svar.
Hvis begge app.entities skal gemmes som én samlet operation, kan du lade ImportVehicleService styre en transaktion, der omfatter begge databaseoperationer:
1. Start en transaktion.
2. Gem Car.
3. Opret og gem ServiceRecord.
4. Commit, hvis begge operationer lykkes.
5. Rollback, hvis en operation fejler.
   Det er vigtigt, at begge DAO-operationer bruger samme transaktion og samme relevante persistence context. Hvis hver DAO opretter sin egen EntityManager og committer uafhængigt, er det ikke nok blot at kalde dem fra samme servicemetode.
   I dit projekt ville det derfor være relevant at undersøge, hvordan CarDao.createCar() og ServiceRecordDao.createServiceRecord() håndterer deres EntityManager og transaktioner.

# 5. Hvorfor kalde det eksterne API før transaktionen?
Du har fat i det rigtige: Det er ofte hensigtsmæssigt at hente og behandle eksterne data, før du åbner en databasetransaktion.
Fordelene er:
- Du holder ikke en transaktion og databaseforbindelse åben, mens du venter på netværket.
- Du kan validere og filtrere data, før du skriver til databasen.
- Du undgår at udføre databasearbejde, hvis API-svaret er ugyldigt.
  Ulempen er, at API-dataene kan ændre sig mellem hentningen og lagringen, og at den eksterne API ikke kan rulles tilbage sammen med din database. En transaktion kan kun garantere atomiskhed for de databaseoperationer, der deltager i den — ikke for det eksterne HTTP-kald.

# 4. Hvorfor slår du brugeren op i databasen?
Din forklaring er god, men en detalje skal rettes: Du behøver ikke nødvendigvis at udelade brugerens ID fra et JWT af sikkerhedsmæssige årsager. Et ID er ikke i sig selv en hemmelighed. Det afgørende er, at tokenet er verificeret, og at serveren kontrollerer brugerens adgang.
I din nuværende løsning bruger du brugernavnet fra UserDTO til at finde den tilsvarende User-entity gennem UserDao.
Det giver controlleren en entity, som kan forbindes med bilen gennem JPA-relationen. Det holder samtidig databaseadgangen adskilt fra controllerens HTTP-logik.

# Gyldig signatur: 
verify(token) kontrollerer blandt andet, om signaturen passer til tokenets indhold og den forventede hemmelighed. Verifikatoren kontrollerer også tokenets udløbstid. 
Hvis nogen ændrer rollen fra USER til ADMIN, vil signaturen normalt ikke længere være gyldig.