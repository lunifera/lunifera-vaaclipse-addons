package org.lunifera.vaaclipse.addons.extension.system;

import com.google.common.base.Objects;
import org.lunifera.ecview.core.common.visibility.IVisibilityHandler;
import org.lunifera.ecview.core.common.visibility.IVisibilityManager;
import org.lunifera.ecview.core.common.visibility.IVisibilityProcessor;
import org.lunifera.runtime.common.datasource.IDataSourceService;

@SuppressWarnings("all")
public class DatasourceActionsVisibilityProcessor implements IVisibilityProcessor {
  private IVisibilityHandler deleteAction;
  
  private IVisibilityHandler saveAction;
  
  private IVisibilityHandler mainLayout;
  
  private IDataSourceService.DataSourceInfo main;
  
  public void init(final IVisibilityManager manager) {
    deleteAction = manager.getById("org.lunifera.actions.delete");
    saveAction = manager.getById("org.lunifera.actions.save");
    mainLayout = manager.getById("DatasourceDetails.Detail");
  }
  
  public void fire() {
    doFire();
    
    deleteAction.apply();
    saveAction.apply();
    mainLayout.apply();
  }
  
  public void doFire() {
    this.mainLayout.setEnabled(true);
    this.deleteAction.setEnabled(false);
    this.saveAction.setEnabled(false);
    boolean _notEquals = (!Objects.equal(this.main, null));
    if (_notEquals) {
      this.deleteAction.setEnabled(true);
    } else {
      this.mainLayout.setEnabled(false);
    }
  }
  
  public IDataSourceService.DataSourceInfo getMain() {
    return this.main;
  }
  
  public void setMain(final IDataSourceService.DataSourceInfo main) {
    this.main=main;
    
    fire();
  }
}
