import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Function;
//TODO: TESTS!!!!!!!!!
public class BotLogic { //TODO: убрать статики
    private static HashMap<String, Command> commandsList = new HashMap<>();
    private static HashMap<String, Pet> pets = new HashMap<>();
    private static String currentPlayerID = "";
    private static PetLife petLife;
    private static String helpOutput;

    private static void write(String output) {
        System.out.println(output);
    }

    public BotLogic(){
        fillCommands();
    }

    private static void makeCommand(String name, String help, Function<String[], String> action) {
        var newCommand = new Command(name, help, action);
        commandsList.put(newCommand.getName(), newCommand);
    }

    private static void fillCommands() {
        makeCommand("help", "help: Выводит список доступных комманд.", BotLogic::helpCommand);
        makeCommand("create", "create [name]: Создаёт нового питомца с именем name.",
                BotLogic::createCommand);
        makeCommand("feed", "feed: Покормить питомца. Прибавляет 1 к сытости.",
                BotLogic::feedCommand);
        makeCommand("play", "play: Поиграть с питомцем. Прибавляет 1 к счастью и отнимает 1 от бодрости.",
                BotLogic::playCommand);
        makeCommand("sleep",
                "sleep [hours]: Отправить питомца спать на hours часов. Прибавляет hours к бодрости.",
                BotLogic::sleepCommand);
        makeCommand("chars", "chars: Получить характеристики питомца",
                BotLogic::getCharacteristicsCommand);
        makeCommand("delete", "delete: Удалить питомца",
                BotLogic::deleteCommand);
        makeCommand("/start", "start",
                BotLogic::greetings);
    }

    public static String commandInput(String playerID, String args) throws IOException {
        currentPlayerID = playerID;
        var input = args.split(" ");
        if (input.length < 1) {
            return error();
        }
        var userCommand = input[0];
        if (!commandsList.containsKey(userCommand)) {
            return error();
        }
        var commandArgs = new String[0];
        if (input.length > 1) {
            commandArgs = Arrays.copyOfRange(input, 1, input.length);
        }
        try {
            return commandsList.get(userCommand).execute(commandArgs);
        } catch (Exception e) { //TODO: сохранять/выводить инфу об ошибке
            return error();
        }
    }

    private static String error() {
        return "Wrong command or input data";
    }

    private static String greetings(String[] args) {
        String hello = "Привет!\nВ этом чат-боте ты можешь создать и ухаживать за своим питомцем.";
        return hello + commandsList.get("help").execute(new String[0]);
    }

    private static String helpCommand(String[] args) {
        if (helpOutput.length() == 0){
                StringBuilder help = new StringBuilder("\nКомманды, которые ты можешь использовать:");
            for (String command : commandsList.keySet()) {
                help.append("\n");
                help.append(commandsList.get(command).help());
            }
            helpOutput = help.toString();
        }
        return helpOutput;
    }

    private static String createCommand(String[] args) {
        var name = args[0];
        if (pets.containsKey(currentPlayerID))
            return error();
        pets.put(currentPlayerID, new Pet(name));

        petLife = new PetLife(pets.get(currentPlayerID));

        Thread myThready = new Thread(petLife);
        myThready.start();

        return pets.get(currentPlayerID).getCharacteristics();

    }

    private static String deleteCommand(String[] args) {
        pets.remove(currentPlayerID);

        return "Питомец удален";
    }

    private synchronized static String feedCommand(String[] args) {
        pets.get(currentPlayerID).feed();
        return "Очень вкусно! +1 к сытости";
    }

    private synchronized static String playCommand(String[] args) {
        pets.get(currentPlayerID).play();
        return "Как весело! +1 к счастью";
    }

    private synchronized static String sleepCommand(String[] args) {
        int hours = Integer.parseInt(args[0]);
        pets.get(currentPlayerID).sleep(hours);
        return "+" + Integer.toString(hours) + " к бодрости";
    }

    private synchronized static String getCharacteristicsCommand(String[] args) {
        return pets.get(currentPlayerID).getCharacteristics();
    }
}