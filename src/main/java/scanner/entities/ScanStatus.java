package scanner.entities;

public enum ScanStatus {
    NotScanned(0),
    CompleteProfile(1),
    CompleteFollowers(2);

    private final int value;

    private ScanStatus(int value){
        this.value = value;
    }

    public int getValue(){
        return this.value;
    }
}
