package com.tecnalia.wicket.pages.ecotool;

import java.text.NumberFormat;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath(value = "/ecotool")
@SuppressWarnings("serial")
public class Index extends CheesrPage {

public Index() {
	
/*
	IModel<List<Cheese>> model = new LoadableDetachableModel<List<Cheese>>() {
		@Override
		protected List<Cheese> load() {
			return getCheeses();
		}
	};	
	
	add(new DataView<Cheese>("cheeses", new ListDataProvider<Cheese>(model.getObject()))
	{
		private static final long serialVersionUID = 1L;

		@Override
		protected void populateItem(Item<Cheese> item) {
			// TODO Auto-generated method stub
			Cheese cheese = (Cheese) item.getModelObject();
	          item.add(new Label("name", cheese.getName()));
	          item.add(new Label("description", cheese.getDescription()));
	          item.add(new Label("price", "$" + cheese.getPrice()));

	          item.add(new Link<Cheese>("add", item.getModel()) {
	            @Override
	            public void onClick() {
	            	Cheese selected = (Cheese) getModelObject();
	            	getCart().getCheeses().add(selected);
	            }
	          });
		}

	});
*/

    PageableListView<Cheese> cheeses = new PageableListView<Cheese>("cheeses", getCheeses(), 2) {
        @Override
        protected void populateItem(ListItem<Cheese> item) {
          Cheese cheese = item.getModelObject();
          item.add(new Label("name", cheese.getName()));
          item.add(new Label("description", cheese.getDescription()));
          item.add(new Label("price", "$" + cheese.getPrice()));
          item.add(new Link<Cheese>("add", item.getModel()) {
            @Override
            public void onClick() {
              Cheese selected = getModelObject();
              getCart().getCheeses().add(selected);
            }
          });
        }
      };
      add(cheeses);    
      add(new PagingNavigator("navigator", cheeses));
	
    add(new ListView<Cheese>("cart", new PropertyModel<List<Cheese>>(this, "cart.cheeses")) {
      @Override
      protected void populateItem(ListItem<Cheese> item) {
        Cheese cheese = item.getModelObject();
        item.add(new Label("name", cheese.getName()));
        item.add(new Label("price", "$" + cheese.getPrice()));
        item.add(new Link<Cheese>("remove", item.getModel()) {
          @Override
          public void onClick() {
            Cheese selected = getModelObject();
            getCart().getCheeses().remove(selected);
          }
        });
      }
    });
    
    // add(new Label("total", "$" + getCart().getTotal()));
    
    add(new Label("total", new Model<String>() {
      @Override
      public String getObject() {
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        return nf.format(getCart().getTotal());
      }
    }));
    
    add(new Link<Object>("checkout") {
      @Override
      public void onClick() {
        setResponsePage(new Checkout());
      }

      @Override
      public boolean isVisible() {
        return !getCart().getCheeses().isEmpty();
      }
    });

  }
}
