package com.pugzarecute.exptracker.networking;

import com.pugzarecute.exptracker.item.ItemRg;
import com.pugzarecute.exptracker.server.Handler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public class RefundItemsIfPlayerlistEmptyC2S {
    public RefundItemsIfPlayerlistEmptyC2S() {

    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {
    }

    public static RefundItemsIfPlayerlistEmptyC2S decode(FriendlyByteBuf friendlyByteBuf) {
        return new RefundItemsIfPlayerlistEmptyC2S();
    }

    public boolean handle(Supplier<NetworkEvent.Context> contextSupplier) {
        final var success = new AtomicBoolean();
        contextSupplier.get().enqueueWork(() -> {
            if (contextSupplier.get().getSender().getPersistentData().getBoolean("exptracker.safety_token")) {
                Handler.cleanup(contextSupplier.get().getSender(), contextSupplier.get().getSender());
                contextSupplier.get().getSender().addItem(ItemRg.TRACKING_COMPASS.get().getDefaultInstance());
            }
            success.set(true);
        });

        contextSupplier.get().setPacketHandled(true);
        return success.get();
    }
}

