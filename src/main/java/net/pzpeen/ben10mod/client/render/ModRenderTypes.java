package net.pzpeen.ben10mod.client.render;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public abstract class ModRenderTypes extends RenderStateShard{

    public ModRenderTypes(String pName, Runnable pSetupState, Runnable pClearState) {
        super(pName, pSetupState, pClearState);
    }

    private static final Map<ResourceLocation, RenderType> CACHE = Maps.newHashMap();

    public static RenderType pyroniteHeadFireGlow(ResourceLocation texture){
        return CACHE.computeIfAbsent(texture, tex -> {
            RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.RENDERTYPE_EYES_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(tex, false, false))
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .setDepthTestState(new DepthTestStateShard("lequal", 515){
                        @Override
                        public void setupRenderState() {
                            super.setupRenderState();
                            RenderSystem.enablePolygonOffset();
                            RenderSystem.polygonOffset(-1.0f, -10.0f);
                        }

                        @Override
                        public void clearRenderState() {
                            RenderSystem.disablePolygonOffset();
                            super.clearRenderState();
                        }
                    })
                    .setOverlayState(RenderStateShard.OVERLAY)
                    .createCompositeState(true);

            return RenderType.create("pyroniteHeadFireGlow", DefaultVertexFormat.NEW_ENTITY,
                    VertexFormat.Mode.QUADS, 256, false, false, compositeState);
        });


    }


}
