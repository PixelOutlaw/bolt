/*
 * This file is part of Blight, licensed under the ISC License.
 *
 * Copyright (c) 2014 Richard Harrah
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted,
 * provided that the above copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
 * INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF
 * THIS SOFTWARE.
 */
package info.faceland.bolt;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.nunnerycode.facecore.plugin.FacePlugin;
import org.nunnerycode.kern.methodcommand.CommandHandler;

public class BoltPlugin extends FacePlugin {

    @Override
    public void enable() {
        Bukkit.getPluginManager().registerEvents(new BoltListener(this), this);
        CommandHandler commandHandler = new CommandHandler(this);
        commandHandler.registerCommands(new BoltCommand());
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
    }

}
