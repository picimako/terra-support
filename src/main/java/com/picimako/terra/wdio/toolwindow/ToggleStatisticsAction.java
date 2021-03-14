package com.picimako.terra.wdio.toolwindow;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import org.jetbrains.annotations.NotNull;

import com.picimako.terra.resources.TerraBundle;

/**
 * Action for toggling the screenshot statistics in the Terra wdio tool window.
 * <p>
 * It appears on the Terra wdio tool window's toolbar.
 *
 * @see ScreenshotStatisticsProjectService
 * @see TerraWdioToolWindowFactory
 * @since 0.5.0
 */
public class ToggleStatisticsAction extends ToggleAction {

    private final Runnable updateUICallback;

    public ToggleStatisticsAction(Runnable updateUICallback) {
        super(TerraBundle.toolWindow("statistics.toggle"), TerraBundle.toolWindow("statistics.toggle.description"), AllIcons.Actions.Show);
        this.updateUICallback = updateUICallback;
    }

    @Override
    public boolean isSelected(@NotNull AnActionEvent e) {
        return ScreenshotStatisticsProjectService.getInstance(e.getProject()).isShowStatistics;
    }

    @Override
    public void setSelected(@NotNull AnActionEvent e, boolean state) {
        ScreenshotStatisticsProjectService.getInstance(e.getProject()).isShowStatistics = state;
        updateUICallback.run();
    }
}
