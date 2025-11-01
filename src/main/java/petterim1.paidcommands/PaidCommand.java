package petterim1.paidcommands;

class PaidCommand {

    final String name;
    final String description;
    final String command;
    final double price;

    PaidCommand(String name, String description, String command, double price) {
        this.name = name;
        this.description = description;
        this.command = command;
        this.price = price;
    }

    public String toString() {
        return "PaidCommand(" + name + ", " + description + ", " + command + ", " + price + ')';
    }
}
