# Pixel War

**Pixel War** je dinamična multiplayer igra sa tablom u realnom vremenu, razvijena u Javi korišćenjem **Swing** biblioteke za grafički interfejs i **Java Sockets (TCP)** za mrežnu komunikaciju. Igra podržava dinamički broj ($N$) igrača koji se takmiče u bojenju mrežne table od $50 \times 50$ piksela.

---

## Arhitektura Projekta

Projekat je podeljen na tri glavna paketa kako bi se jasno razdvojila grafika od mrežne logike i zajedničkih struktura podataka:

### 1. `client` (Klijentski deo)
* **`GameClient`**: Ulazna tačka klijenta. Pokreće mrežni sistem.
* **`NetworkHandler`**: "Srce" mrežne komunikacije na klijentu. Otvara `Socket` ka serveru, šalje klikove i u posebnoj pozadinskoj niti sluša osveženja sa mreže.
* **`client.ui`**: Sadrži grafičke komponente (`GameWindow` sa tajmerom, `PixelCanvas` za crtanje i dinamički `ScorePanel` koji podržava skrolovanje i prikaz rezultata za $N$ igrača).

### 2. `server` (Serverski deo)
* **`GameServer`**: Glavni autoritet igre. Prihvata konekcije, dodeljuje igračima jedinstvene ID-eve (i boje), vodi računa o globalnoj matrici i preračunava bodove u realnom vremenu nakon svakog klika.

### 3. `shared` (Zajednički resursi)
* **`Message`**: Serijalizabilna (`Serializable`) klasa koja služi kao standardizovana koverta za prenos podataka kroz mrežne cevi. Prenosi tipove akcija (`PRIJAVA`, `KLIK`, `UPDATE_GAME`), matricu, imena i skorove.

---

## Kako pokrenuti igru (Lokalno)

Da biste uspešno pokrenuli igru na svom računaru unutar razvojnog okruženja (npr. IntelliJ IDEA), pratite ove korake:

### Korak 1: Pokretanje Servera
1. Locirajte fajl `src/server/GameServer.java`.
2. Pokrenite ga pomoću opcije **Run 'GameServer.main()'**.
3. U konzoli ćete videti poruku: `[SERVER] Pokretanje servera na portu 8888...`

### Korak 2: Pokretanje Klijenata (Igrača)
1. Locirajte fajl `src/client/GameClient.java`.
2. Pokrenite ga pomoću opcije **Run 'GameClient.main()'**.
3. Iskočiće dijalog sa zahtevom: *"Unesi svoje ime:"*. Nakon unosa, otvara se prozor igre.
4. **Za multiplayer simulaciju**: Ponovite pokretanje `GameClient.main()` još nekoliko puta kako biste kreirali druge igrače (svaki klijent će dobiti svoju jedinstvenu boju na tabeli rezultata).

---

## Pravila igre i funkcionalnosti

* **Cilj igre**: Sakupiti što više obojenih piksela na tabli pre nego što tajmer (42 sekunde) istekne.
* **Mrežna sinhronizacija**: Klikom na sivo polje klijent šalje zahtev serveru. Server odobrava promenu i šalje novu matricu svim povezanim klijentima istovremeno.
* **Skorovi za N igrača**: Desni panel (`ScorePanel`) se dinamički osvežava i prilagođava trenutnom broju prijavljenih korisnika.

---

## Tehnologije

* **Java SE 17+** (ili vaša verzija)
* **Java Swing & AWT** (za GUI)
* **Java Sockets** (TCP komunikacija kroz `ObjectOutputStream` i `ObjectInputStream`)
* **Multithreading** (korišćenje pozadinskih niti za nesmetano slušanje mrežnog saobraćaja bez blokiranja grafičkog interfejsa)
