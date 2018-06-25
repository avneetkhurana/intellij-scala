package org.jetbrains.plugins.scala.worksheet.settings;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: Dmitry.Naydanov
 */
@State(
    name = "WorksheetDefaultProjectSettings",
    storages = {
        @Storage(StoragePathMacros.WORKSPACE_FILE),
        @Storage("scala_settings.xml")
    }
)
public class WorksheetDefaultSettings implements PersistentStateComponent<WorksheetDefaultSettings> {
  private boolean isRepl;
  private boolean isInteractive;
  private boolean isMakeBeforeRun;

  private String moduleName;
  private String compilerProfileName;
  private WorksheetRunType runType = WorksheetRunType.REPL_CELL;

  public boolean getIsRepl() {
    return isRepl;
  }

  public void setRepl(boolean isRepl) {
    this.isRepl = isRepl;
  }

  public boolean isInteractive() {
    return isInteractive;
  }

  public void setInteractive(boolean interactive) {
    isInteractive = interactive;
  }

  public boolean isMakeBeforeRun() {
    return isMakeBeforeRun;
  }

  public void setMakeBeforeRun(boolean makeBeforeRun) {
    isMakeBeforeRun = makeBeforeRun;
  }

  public String getModuleName() {
    return moduleName;
  }

  public String getCompilerProfileName() {
    return compilerProfileName;
  }

  public void setCompilerProfileName(String compilerProfileName) {
    this.compilerProfileName = compilerProfileName;
  }

  public void setModuleName(String moduleName) {
    this.moduleName = moduleName;
  }

  @Nullable
  @Override
  public WorksheetDefaultSettings getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull WorksheetDefaultSettings worksheetDefaultSettings) {
    XmlSerializerUtil.copyBean(worksheetDefaultSettings, this);
  }

  @Override
  public void noStateLoaded() {

  }
  
  public static WorksheetDefaultSettings getInstance(Project project) {
    return ServiceManager.getService(project, WorksheetDefaultSettings.class);
  }

  public WorksheetRunType getRunType() {
    return runType;
  }

  public void setRunType(WorksheetRunType runType) {
    this.runType = runType;
  }
}
