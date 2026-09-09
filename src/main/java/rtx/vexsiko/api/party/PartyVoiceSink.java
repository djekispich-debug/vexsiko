package rtx.vexsiko.api.party;

@FunctionalInterface
public interface PartyVoiceSink {
    void accept(String username, int seq, byte[] opusData);
}