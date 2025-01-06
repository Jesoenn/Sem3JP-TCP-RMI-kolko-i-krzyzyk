# Kółko i krzyżyk (klient - serwer)
### Zrobione
- klient łączy się z serwerem. Może stworzyć swój pokój, albo dołączyć do innego, w którym jest 1 osoba.
- wychodzenie z pokoju
- markowanie ready

### Struktura plików
```
tic-tac-toe/
│
├── server/
|   ├── Main.java
|   ├── controller/
│   │   ├── GameServer.java      
│   │   ├── GameController.java  
│   │
│   ├── model/
│   │   ├── GameEngine.java      
│   │   ├── GameRoom.java         
│   │   ├── PlayerStats.java      
│   │
│   ├── view/
│       ├── ServerLogger.java    
│
├── client/
│   ├── Main.java
│   ├── controller/
│   │   ├── ClientController.java  
│   │
│   ├── model/
│   │   ├── Client.java           
│   │
│   ├── view/
│   │   ├── UI.java               
│
├── shared/
│   ├── GameServerInterface.java 
│   ├── ClientInterface.java  
│   ├── enumy
```



## Treść programowa:
1. Rozpraszanie obliczeń poprzez wykorzystanie gniazd TCP/IP.
2. Rozpraszanie obliczeń poprzez zdalne wywoływanie procedur. (RMI)
## Funkcjonalności
1. **Hybrydowa komunikacja sieciowa** - użycie zarówno komunikacji poprzez RMI jak i gniazd TCP/IP.
2. **Mechanika gry** - implementacja silnika zarządzającego mechaniką gry w kółko i krzyżyk
- Należy wykorzystać mechanizm RMI w celu nawiązania połączenia między dwoma graczami oraz obsługę mechaniki gry (np. parowanie graczy, inicjalizowanie stanu gry, wymiana ruchów, walidacja I/O).
- Mechanika gry powinna być zaimplementowana w aplikacji serwera.
- Należy przemyśleć argumenty pobierane przez aplikacje JAR serwer/klient.
- Serwer powinen obsługiwać więcej niż jednego gracza w tym samym momencie (np. poprzez realizację gier w dedykowanych pokojach lub tworzenie tokenów poszczególnych sesji wymienianych przez graczy).
- Serwer powinen obliczać statystyki graczy w danej sesji - ilość wygranych, remisów oraz porażek.
- Aplikacja serwera powinna logować w konsoli kluczowe dane (parowanie użytkowników, błędy, koniec gry, itp).
- zalecane java.nio

## GRUPA A
klient podlacza sie do serwera, nie ma nikogo innego to czeka <br>
podlacza sie drugi gracz i moze zdecydowac czy chce grac z 1 czy czeka<br>
Czat bez udzialu serwera<br>
Serwer prowadzi informacje o polaczonych klientach (adresach ip) i kazdy kazdy gracz moze sobie pobrac ip drugiego gracza i nawiazac komunikacje po danym porcie. <br>

- chat pomiędzy użytkownikami z TCP/IP
- Gniazda powinny być otworzone pomiędzy grającymi użytkownikami
- Serwer udostępnia iformacjęo adresach IP użytkowników
![image](https://github.com/user-attachments/assets/e624110e-2d44-481f-a49f-e2ecf4c448df)

## Wymagania
1. Minimum dwie aplikacje (serwer, klient, obserwator*)
2. Zaimplementowane zasady gry, statystyki
3. Komunikacja z wykorzystaniem protokołu RMI
4. Obsługa N klientów (skalowalność)
5. Obsługa mechaniki pokojów (gracze nie są parowami losowego)
6. Implementacja funkcjonalności chatu pomiędzy dwoma graczami z wykorzystaniem mechanizmu gniazd TCP/IP.

## Ocena
1. Aplikacja rozdzielona na 2 archiwa jar (klient, serwer)
2. Zaimplementowana poprawna mechanika gry
3. Mechanizm zbierania podstawowych statystyk (wygrane, porazki, remisy)
4. Rozgrywka klient-serwer za pomocą RMI
5. Obsługa N klientów
6. Mechanizm parowanie graczy (pokoje, tokeny, ...)
7. Implementacja funkcjonalności chatu za pomocą protokołu TCP/IP
