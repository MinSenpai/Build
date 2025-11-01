package petterim1.paidcommands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.plugin.PluginBase;
import me.onebone.economyapi.EconomyAPI;

import java.util.HashMap;
import java.util.Map;

public class PaidCommands extends PluginBase {

    static final Map<String, PaidCommand> commands = new HashMap<>();

    private boolean logPayments;

    String txt_shopform_title;
    String txt_shopform_content;
    String txt_confirmationform_title;
    String txt_confirmationform_yes;
    String txt_confirmationform_no;
    String txt_nomoneyform_title;
    String txt_nomoneyform_content;
    String txt_confirmationform_price;
    String txt_buy_failed;
    String txt_buy_success;

    public void onEnable() {
        loadData();
        getServer().getPluginManager().registerEvents(new Listeners(this), this);
    }

    private void loadTranslations() {
        logPayments = getConfig().getBoolean("logPayments");
        txt_shopform_title = getConfig().getString("Translations.shopform.title");
        txt_shopform_content = getConfig().getString("Translations.shopform.content");
        txt_confirmationform_title = getConfig().getString("Translations.confirmationform.title");
        txt_confirmationform_yes = getConfig().getString("Translations.confirmationform.yes");
        txt_confirmationform_no = getConfig().getString("Translations.confirmationform.no");
        txt_nomoneyform_title = getConfig().getString("Translations.nomoneyform.title");
        txt_nomoneyform_content = getConfig().getString("Translations.nomoneyform.content");
        txt_confirmationform_price = getConfig().getString("Translations.confirmationform.price");
        txt_buy_failed = getConfig().getString("Translations.buy.failed");
        txt_buy_success = getConfig().getString("Translations.buy.success");
    }

    private void loadData() {
        saveDefaultConfig();
        Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) getConfig().get("Commands");
        map.forEach((name, command) -> {
            commands.put(name, new PaidCommand(name,
                    (String) command.get("description"),
                    (String) command.get("command"),
                    (double) command.get("price")));
        });
        getLogger().debug(commands.toString());
        loadTranslations();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("buycommand")) {
            if (!(sender instanceof Player)) {
                return false;
            }
            showMainForm((Player) sender);
            return true;
        }
        return false;
    }

    void showMainForm(Player p) {
        FormWindowSimple form = new FormWindowSimple(txt_shopform_title, txt_shopform_content);
        commands.forEach((name, command) -> form.addButton(new ElementButton(name + "\n§7" + command.description)));
        p.showFormWindow(form);
    }

    void showConfirmation(Player p, PaidCommand c) {
        if (EconomyAPI.getInstance().myMoney(p) >= c.price) {
            FormWindowModal form = new FormWindowModal(txt_confirmationform_title, c.name + "\n\n" + c.description + "\n\n" + txt_confirmationform_price + c.price, txt_confirmationform_yes, txt_confirmationform_no);
            p.showFormWindow(form);
        } else {
            FormWindowSimple form = new FormWindowSimple(txt_nomoneyform_title, txt_nomoneyform_content);
            p.showFormWindow(form);
        }
    }

    void buy(Player p, PaidCommand c) {
        int result = EconomyAPI.getInstance().reduceMoney(p, c.price);
        if (result == 1) {
            String[] cmd = c.command.split(";");
            for (String s : cmd) {
                if (s.startsWith("[player] ")) {
                    getServer().dispatchCommand(p, s.replace("[player] ", "").replace("%player%", '"' + p.getName() + '"'));
                } else if (s.startsWith("[console] ")) {
                    getServer().dispatchCommand(getServer().getConsoleSender(), s.replace("[console] ", "").replace("%player%", '"' + p.getName() + '"'));
                } else {
                    p.sendMessage(txt_buy_failed + "Config error");
                    return;
                }
            }
            p.sendMessage(txt_buy_success);
            if (logPayments) {
                getLogger().info(p.getName() + " bought '" + c.name + '\'');
            }
        } else {
            p.sendMessage(txt_buy_failed + result);
        }
    }
}
