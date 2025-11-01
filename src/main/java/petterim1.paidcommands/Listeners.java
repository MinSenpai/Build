package petterim1.paidcommands;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.response.FormResponseModal;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.form.window.FormWindowSimple;

public class Listeners implements Listener {

    private final PaidCommands pl;

    public Listeners(PaidCommands pl) {
        this.pl = pl;
    }

    @EventHandler
    public void onForm(PlayerFormRespondedEvent e) {
        if (e.getWindow() instanceof FormWindowSimple && e.getResponse() instanceof FormResponseSimple) {
            if (((FormWindowSimple) e.getWindow()).getTitle().equals(pl.txt_shopform_title)) {
                if (((FormResponseSimple) e.getResponse()).getClickedButton() != null) {
                    String[] in = ((FormResponseSimple) e.getResponse()).getClickedButton().getText().split("\n");
                    pl.showConfirmation(e.getPlayer(), PaidCommands.commands.get(in[0]));
                }
            } else if (((FormWindowSimple) e.getWindow()).getTitle().equals(pl.txt_nomoneyform_title)) {
                pl.showMainForm(e.getPlayer());
            }
        } else if (e.getWindow() instanceof FormWindowModal && e.getResponse() instanceof FormResponseModal) {
            if (((FormWindowModal) e.getWindow()).getTitle().equals(pl.txt_confirmationform_title)) {
                if (((FormResponseModal) e.getResponse()).getClickedButtonText().equals(pl.txt_confirmationform_yes)) {
                    String[] in = ((FormWindowModal) e.getWindow()).getContent().split("\n");
                    PaidCommand cmd = PaidCommands.commands.get(in[0]);
                    if (cmd == null) {
                        pl.getLogger().error("[Confirmation form | get] Invalid response: " + in[0]);
                    } else {
                        pl.buy(e.getPlayer(), cmd);
                    }
                } else if (((FormResponseModal) e.getResponse()).getClickedButtonText().equals(pl.txt_confirmationform_no)) {
                    pl.showMainForm(e.getPlayer());
                } else {
                    pl.getLogger().error("[Confirmation form | click] Invalid response: " + ((FormResponseModal) e.getResponse()).getClickedButtonText());
                }
            }
        }
    }
}
