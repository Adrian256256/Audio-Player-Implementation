_Harea Teodor-Adrian_
_323CA_

*Proiect GlobalWaves*
*Etapa 2 - Pagination*

**Flow Program**

Am folosit implementarea personala, din etapa 1. Aceasta a fost imbunatatita
si modificata astfel in cat sa permita extinderea functionalitatilor.

Proiectul urmareste simularea unei aplicatii care reda muzica si podcasturi.
Aici se ruleaza melodii simple, melodii ce fac parte dintr-un playlist, melodii
ce fac parte dintr-un album si podcasturi. Astfel, trebuie sa mangeriem diferite
informatii in cadrul unei baze de date pentru a facilita experienta unui
utilizator in ceea ce priveste folosirea programului nostru.

Pentru a gestiona informatiile, avem create 2 pachete ce contin diferite clase
care au scopuri bine alese. Fiecare clasa are rolul ei in mentinerea unei rulari
lipsite de bug-uri. Sunt facute verificari si calcule pentru prelucrarea datelor
in fiecare loc unde este nevoie, fiind evitata orice pierdere de informatie.

Pentru gestionarea functionalitatilor intr-un mod cat mai usor de inteles,
avem metode specifice fiecarei comenzi, care poarta nume sugestive. (ex: metoda
_load_ este asociata comenzii *load*).

Codul este gandit astfel incat sa se potata face o eventuala extindere a 
functionalitatilor. Inceputul consta in citirea comenzilor din fisierul json,
apoi se ia fiecare comanda si, prin conditionalele "if" se apeleaza metodele
specifice fiecarei comenzi.

**Pachete**
    
-database: contine in general clase auxiliare care stau la baza implementarii.
          Ex: ArtistPage este clasa care contine informatii despre pagina
          unui artist, iar Event este clasa care contine informatiile despre
          un eveniment.

-programInterface: contine clasele care sunt cele mai apropiate de utilizator,
            astfel aici gasim clase cu metode pentru comenzi si campuri ce au
            ca scop inceperea legaturilor intre clase prin agregari, compuneri
            sau mosteniri.

**Despre Clase**

Se foloseste *Design Pattern*-ul *Singleton*, pentru clasa _ExecuteCommand_,
precum si pentru alte clase care nu ofera un rost unei instantieri multiple.
    *Singleton*: ExecuteCommand, SearchBar, GeneralStatistics, etc.

Fiecare clasa contine metode specifice numelui ei. Spre exemplu, _GeneralStatistics_
contine metodele ce realizeaza comenzi specifice statisticilor generale. Astfel, codul
este mai intuitiv si usor de inteles.

Utilizatorii si informatiile despre acestia sunt retinuti in array-ul de clase
"UserDatabase" continut in clasa "UserManagement", care contine metode pentru comenzile
ce tin de baza de date a utilizatorilor.

Paginile si informatiile despre acestea sunt retinute in array-urile de clase
"ArtistPage" si "HostPage" continute in clasa "PageManagementHub", care contine metode
ce tin de baza de date specifica pentru paginile utilizatorilor. 

Clasa ExecuteCommand gazduieste array-ul de comenzi citite din fisierul json, prin
continerea unui array de clase de tip Command. ExecuteCommand apeleaza metode specifice
pentru fiecare comanda.

Clasa *ExecuteCommand* este cea care apeleaza metodele propriu zise ale
fiecarei comenzi, si pregateste output-ul. 
    Prin intermediul unui *lant de mostenire* (_ExecuteCommand_, _NavigateClass_,
_Player_, _StatusClass_, _PlayListClass_) se pot apela in cadrul clasei ExecuteCommand
metode specifice tuturor claselor extinse. Am gandit o astfel de implementare
pentru fragmentarea functionalitatilor programului (metodele specifice fiecarei
comenzi) in mai multe clase pentru o intelegere mai usoara. Astfel, prin "super"
apelam metodele oricarei clase din acestea, fara a le mai primi ca parametru sau
a realiza alte instantieri.

Prin intermediul *compunerii*, clasa PrincipalDatabase se foloseste de alte
clase pentru a structura o baza de date care sa contina date despre incarcari,
cautari, like-uri, etc(informatii care trebuie retinute pentru functionarea
aplicatiei).

*despre **PrincipalDatabase***: scopul acestei clase este de a stoca cat mai multe
informatii in ea pentru o modularizare mai eficienta. Astfel, avem campurile:

-_loaded_(o lista de clase care contine incarcarile utilizatorilor)

-_likesByUsernames_(lista de clase care contine utilizatorii care au apreciat
o melodie, sau mai multe; se stocheaza si numele melodiilor apreciate, pentru
metodele care se ocupa de statistica, pagini, etc.)

-_likesBySongs_(lista de clase care stocheaza melodiile apreciate si utilizatorii care
au apreciat aceste melodii)

-_searchData_(o lista de clase care contine informatiile in ceea ce priveste cautarile
utilizatorilor, aici avem numele utilizatorului care a facut cautarea si rezultatele
obtinute in urma cautarii)

-_selected_(o lista de clase care contine selectiile utilizatorilor; avem numele
utilizatorului si entitatea selectata, precum si informatii despre tipul entitatii
selectate sau proprietati ale acesteia)

-_statuses_(o lista care contine starea in care se afla un utilizator - adica ce asculta,
cat timp a trecut din entitatea ascultata, modul de repeat/shuffle, albumul in care se afla,
etc.)

-_playlistPrincipal_(lista de clase care stocheaza playlist-urile existente in baza de date si
informatiile despre acestea)

-_users_ si _pages_ care sunt clase ce au ca scop management-ul utilizatorilor si a paginilor

Am folosit GitHub Copilot la verificarea corectitudinii datei in clasa "PageManagementHub",
in metoda "addEvent" si la ordonarea lexicografica din clasa "GeneralStatistics" in metodele
"getTop5Artists" si "getTop5Albums".
