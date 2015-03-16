package com.tecnalia.wicket.pages;


import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

import com.tecnalia.wicket.components.scaffolding.FluidGrid;
import com.tecnalia.wicket.components.scaffolding.Global;
import com.tecnalia.wicket.components.scaffolding.Grid;
import com.tecnalia.wicket.components.scaffolding.Layouts;
import com.tecnalia.wicket.components.scaffolding.Responsive;

/**
 * The {@code Scaffolding} page
 *
 * @author miha
 * @version 1.0
 */
@MountPath(value = "/scaffolding")
public class Scaffolding extends BasePage {

    /**
     * Construct.
     *
     * @param parameters the current page parameters.
     */
    public Scaffolding(PageParameters parameters) {
        super(parameters);

        add(new Global("global"));
        add(new Grid("grid"));
        add(new FluidGrid("fluidGridSystem"));
        add(new Layouts("layouts"));
        add(new Responsive("responsive"));
    }

    @Override
    protected boolean hasNavigation() {
        return true;
    }
}
