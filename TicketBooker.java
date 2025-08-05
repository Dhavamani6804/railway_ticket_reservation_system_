
import java.util.*;

public class TicketBooker {

    private static TicketBooker instance = null;

    private TicketBooker(){}

    public static TicketBooker getInstance(){
        if(instance==null){
            instance = new TicketBooker();
        }
        return instance;
    }

    static int availableLowerBerths = 1;
    static int availableMiddleBerths = 1;
    static int availableUpperBerths = 1;
    static int availableRacTickets =1;
    static int availableWaitingList = 1;

    static Queue<Integer> waitingList = new LinkedList<>();
    static Queue<Integer> racList = new LinkedList<>();
    static List<Integer> bookedTicketList = new ArrayList<>();
    static List<Passenger> confirmedList = new ArrayList<>();


    static List<Integer> lowerBerthsPositions = new ArrayList<>(Arrays.asList(1));
    static List<Integer> middleBerthsPositions = new ArrayList<>(Arrays.asList(1));
    static List<Integer> upperBerthsPositions = new ArrayList<>(Arrays.asList(1));
    static List<Integer> racPositions = new ArrayList<>(Arrays.asList(1));
    static List<Integer> waitingListPositions = new ArrayList<>(Arrays.asList(1));

    static {
        for (int i = 1; i <= availableLowerBerths; i++) {
            lowerBerthsPositions.add(i);
        }
        for (int i = 1; i <= availableMiddleBerths; i++) {
            middleBerthsPositions.add(i);
        }
        for (int i = 1; i <= availableUpperBerths; i++) {
            upperBerthsPositions.add(i);
        }
        for (int i = 1; i <= availableRacTickets; i++) {
            racPositions.add(i);
        }
        for (int i = 1; i <= availableWaitingList; i++) {
            waitingListPositions.add(i);
        }
    }

    static Map<Integer, Passenger> passengers = new HashMap<>();

    public void bookTicket(Passenger p, int berthInfo, String allotedBerth) {
        p.number = berthInfo;
        p.alloted = allotedBerth;
        passengers.put(p.passengerId, p);
        bookedTicketList.add(p.passengerId);
        confirmedList.add(p);

        System.out.println(".......Booked Successfully.......");
    }

    public void addToRAC(Passenger p, int racInfo, String allotedRAC) {
        p.number = racInfo;
        p.alloted = allotedRAC;
        passengers.put(p.passengerId, p);
        racList.add(p.passengerId);
        availableRacTickets--;
        racPositions.remove(0);
        System.out.println(".......Added to RAC.......");
    }

    public void addToWaitingList(Passenger p, int waitingListInfo, String allotedWL) {
        p.number = waitingListInfo;
        p.alloted = allotedWL;
        passengers.put(p.passengerId, p);
        waitingList.add(p.passengerId);
        availableWaitingList--;
        waitingListPositions.remove(0);
        System.out.println(".......Added to waiting list.......");
    }

    public void cancelTicket(int passengerId) {
        Passenger p = passengers.get(passengerId);
        if (p == null) {
            System.out.println("Passenger ID not found!");
            return;
        }

        passengers.remove(passengerId);
        bookedTicketList.remove(Integer.valueOf(passengerId));
        confirmedList.remove(p);

        int positionBooked = p.number;

        if (p.alloted.equals("L")) {
            availableLowerBerths++;
            lowerBerthsPositions.add(positionBooked);
        } else if (p.alloted.equals("M")) {
            availableMiddleBerths++;
            middleBerthsPositions.add(positionBooked);
        } else if (p.alloted.equals("U")) {
            availableUpperBerths++;
            upperBerthsPositions.add(positionBooked);
        }

        System.out.println(".......Ticket cancelled successfully.......");

        if (!racList.isEmpty()) {
            int racPassengerId = racList.poll();
            Passenger passengerFromRAC = passengers.get(racPassengerId);

            int berthNumber = -1;
            String allotedBerth = "";

            if (availableLowerBerths > 0) {
                berthNumber = lowerBerthsPositions.remove(0);
                availableLowerBerths--;
                allotedBerth = "L";
            } else if (availableMiddleBerths > 0) {
                berthNumber = middleBerthsPositions.remove(0);
                availableMiddleBerths--;
                allotedBerth = "M";
            } else if (availableUpperBerths > 0) {
                berthNumber = upperBerthsPositions.remove(0);
                availableUpperBerths--;
                allotedBerth = "U";
            }

            racPositions.add(passengerFromRAC.number);
            availableRacTickets++;

            passengerFromRAC.number = berthNumber;
            passengerFromRAC.alloted = allotedBerth;

            bookedTicketList.add(racPassengerId);
            confirmedList.add(passengerFromRAC);

            System.out.println("Passenger ID " + racPassengerId + " moved from RAC to confirmed berth: " + allotedBerth + berthNumber);

            if (!waitingList.isEmpty()) {
                int waitingPassengerId = waitingList.poll();
                Passenger passengerFromWL = passengers.get(waitingPassengerId);

                int racPos = racPositions.remove(0);
                availableRacTickets--;

                passengerFromWL.number = racPos;
                passengerFromWL.alloted = "RAC";

                racList.add(waitingPassengerId);
                System.out.println("Passenger ID " + waitingPassengerId + " moved from Waiting List to RAC: RAC" + racPos);

                waitingListPositions.add(passengerFromWL.number);
                availableWaitingList++;
            }
        }

    }


    public void printCurrentStatus() {
        System.out.println("\n=== Current Reservation Status ===");

        System.out.println("\nConfirmed Bookings:");
        for (Passenger p : confirmedList) {
            System.out.println("Passenger ID: " + p.passengerId +
                    ", Name: " + p.name +
                    ", Age: " + p.age +
                    ", Berth: " + p.alloted + p.number);
        }

        System.out.println("\nAvailable Berths:");
        System.out.println("Lower Berths: " + availableLowerBerths);
        System.out.println("Middle Berths: " + availableMiddleBerths);
        System.out.println("Upper Berths: " + availableUpperBerths);

        System.out.println("\nRAC Queue:");
        for (int id : racList) {
            Passenger p = passengers.get(id);
            if (p != null)
                System.out.println("Passenger ID: " + p.passengerId + ", Name: " + p.name + ", Age: " + p.age);
        }

        System.out.println("\nWaiting List:");
        for (int id : waitingList) {
            Passenger p = passengers.get(id);
            if (p != null)
                System.out.println("Passenger ID: " + p.passengerId + ", Name: " + p.name + ", Age: " + p.age);
        }

    }


    public void printAvailable() {
        System.out.println("--------------------------");
        System.out.println("Available Lower Berths " + availableLowerBerths);
        System.out.println("Available Middle Berths " + availableMiddleBerths);
        System.out.println("Available Upper Berths " + availableUpperBerths);
        System.out.println("Available RACs " + availableRacTickets);
        System.out.println("Available Waiting List " + availableWaitingList);
        System.out.println("--------------------------");
    }

    public void printPassengers() {
        if (passengers.size() == 0) {
            System.out.println("No details of passengers");
            return;
        }
        for (Passenger p : passengers.values()) {
            System.out.println("PASSENGER ID: " + p.passengerId);
            System.out.println("Name        : " + p.name);
            System.out.println("Age         : " + p.age);
            System.out.println("Status      : " + p.number + " " + p.alloted);
            System.out.println("--------------------------");
        }
    }
}
