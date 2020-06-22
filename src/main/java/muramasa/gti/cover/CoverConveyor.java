package muramasa.gti.cover;

import muramasa.antimatter.cover.CoverInstance;
import muramasa.antimatter.cover.CoverTiered;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.util.Utils;
import muramasa.gti.Ref;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class CoverConveyor extends CoverTiered {

    public static String ID = "conveyor";

    static int[] speeds = {400,100,20,10,1};

    public CoverConveyor(Tier tier) {
        super(tier);
    }

    public CoverConveyor() {
        super();
    }

  /*  @Override
    public String getId() {
        return tier == null ? ID : ID + "_" + tier.getId();
    }*/

    @Override
    protected String ID() {
        return ID;
    }

    @Override
    public String getDomain() {
        return Ref.ID;
    }

    @Override
    public boolean onInteract(CoverInstance instance, PlayerEntity player, Hand hand, Direction side, @Nullable AntimatterToolType type) {
        if (!player.getEntityWorld().isRemote()) {
            NetworkHooks.openGui((ServerPlayerEntity) player, instance, packetBuffer -> {
                packetBuffer.writeBlockPos(instance.getTile().getPos());
                packetBuffer.writeInt(side.getIndex());
            });
        }
        return true;//super.onInteract(instance, player, hand, side, type);
    }


    @Override
    public void onPlace(CoverInstance instance, Direction side) {
        super.onPlace(instance, side);
    }

    //@Override
    //public Cover onPlace(ItemStack stack) {
    //    return new CoverConveyor(this.tier);
    //}

    @Override
    public void onUpdate(CoverInstance instance, Direction side) {
        if (instance.getTile() == null || instance.getTile().getWorld().getGameTime() % (speeds[tier.getIntegerId()]) != 0) return;
        TileEntity adjTile = instance.getTile().getWorld().getTileEntity(instance.getTile().getPos().offset(side));
        if (adjTile == null) return;
        //DEBUG, just puts this item
        //((TileEntityMachine)instance.getTile()).itemHandler.get().addOutputs(new ItemStack(this.getItem(),1));
        Utils.transferItemsOnCap(instance.getTile(), adjTile);
    }

    @Override
    public boolean hasGui() {
        return true;
    }

    @Override
    protected CoverTiered getTiered(Tier tier) {
        return new CoverConveyor(tier);
    }
}
