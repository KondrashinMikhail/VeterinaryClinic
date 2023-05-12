package form;

import enums.notificationEnums.NotificationLocation;
import enums.notificationEnums.NotificationType;
import form.customGraphics.notification.RoundedPane;
import form.customGraphics.notification.ShadowRenderer;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.animation.timing.TimingTargetAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class NotificationForm extends JComponent {

    private JDialog dialog;
    private Animator animator;
    private final Frame frame;
    private boolean showing;
    private Thread thread;
    private final int animate = 10;
    private BufferedImage imageShadow;
    private final int shadowSize = 6;
    private final NotificationType type;
    private final NotificationLocation location;
    private JLabel labelIcon;
    private JButton buttonCancel;
    private JLabel labelMessage;
    private JLabel labelMessageText;
    private JPanel mainPanel;

    public NotificationForm(Frame frame, NotificationType type, NotificationLocation location, String message) {
        this.frame = frame;
        this.type = type;
        this.location = location;
        init(message);
        initAnimator();
        buttonCancel.addActionListener(e -> closeNotification());
    }

    private void init(String message) {
        setBackground(Color.WHITE);
        dialog = new JDialog(frame);
        dialog.setUndecorated(true);
        dialog.setFocusableWindowState(false);
        dialog.setBackground(new Color(0, 0, 0, 0));
        dialog.setSize(new Dimension(mainPanel.getPreferredSize().width + (message.length() * 5), mainPanel.getPreferredSize().height));
        dialog.setContentPane(new RoundedPane());
        dialog.add(mainPanel);

        switch (type) {
            case INFO -> {
                labelIcon.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/info.png"))));
                labelMessage.setText("Информация");
            }
            case WARNING -> {
                labelIcon.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/error.png"))));
                labelMessage.setText("Ошибка");
            }
            case SUCCESS -> {
                labelIcon.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/success.png"))));
                labelMessage.setText("Успешно");
            }
        }
        labelMessageText.setText(message);
    }

    private void initAnimator() {
        TimingTarget target = new TimingTargetAdapter() {
            private int x;
            private int top;
            private boolean top_to_bot;

            @Override
            public void timingEvent(float fraction) {
                if (showing) {
                    float alpha = 1f - fraction;
                    int y = (int) ((1f - fraction) * animate);
                    if (top_to_bot) {
                        dialog.setLocation(x, top + y);
                    } else {
                        dialog.setLocation(x, top - y);
                    }
                    dialog.setOpacity(alpha);
                } else {
                    int y = (int) (fraction * animate);
                    if (top_to_bot) {
                        dialog.setLocation(x, top + y);
                    } else {
                        dialog.setLocation(x, top - y);
                    }
                    dialog.setOpacity(fraction);
                }
            }

            @Override
            public void begin() {
                if (!showing) {
                    dialog.setOpacity(0f);
                    int margin = 10;
                    int y;
                    if (location == NotificationLocation.TOP_CENTER) {
                        x = frame.getX() + ((frame.getWidth() - dialog.getWidth()) / 2);
                        y = frame.getY();
                        top_to_bot = true;
                    } else if (location == NotificationLocation.TOP_RIGHT) {
                        x = frame.getX() + frame.getWidth() - dialog.getWidth() - margin;
                        y = frame.getY();
                        top_to_bot = true;
                    } else if (location == NotificationLocation.TOP_LEFT) {
                        x = frame.getX() + margin;
                        y = frame.getY();
                        top_to_bot = true;
                    } else if (location == NotificationLocation.BOTTOM_CENTER) {
                        x = frame.getX() + ((frame.getWidth() - dialog.getWidth()) / 2);
                        y = frame.getY() + frame.getHeight() - dialog.getHeight();
                        top_to_bot = false;
                    } else if (location == NotificationLocation.BOTTOM_RIGHT) {
                        x = frame.getX() + frame.getWidth() - dialog.getWidth() - margin;
                        y = frame.getY() + frame.getHeight() - dialog.getHeight();
                        top_to_bot = false;
                    } else if (location == NotificationLocation.BOTTOM_LEFT) {
                        x = frame.getX() + margin;
                        y = frame.getY() + frame.getHeight() - dialog.getHeight();
                        top_to_bot = false;
                    } else {
                        x = frame.getX() + ((frame.getWidth() - dialog.getWidth()) / 2);
                        y = frame.getY() + ((frame.getHeight() - dialog.getHeight()) / 2);
                        top_to_bot = true;
                    }
                    top = y;
                    dialog.setLocation(x, y);
                    dialog.setVisible(true);
                }
            }

            @Override
            public void end() {
                showing = !showing;
                if (showing) {
                    thread = new Thread(() -> {
                        sleep();
                        closeNotification();
                    });
                    thread.start();
                } else {
                    dialog.dispose();
                }
            }
        };
        animator = new Animator(500, target);
        animator.setResolution(5);
    }

    public void showNotification() {
        animator.start();
    }

    private void closeNotification() {
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
        if (animator.isRunning()) {
            if (!showing) {
                animator.stop();
                showing = true;
                animator.start();
            }
        } else {
            showing = true;
            animator.start();
        }
    }

    private void sleep() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {
        }
    }

    @Override
    public void paint(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.drawImage(imageShadow, 0, 0, null);
        int x = shadowSize * 2;
        int y = shadowSize * 2;
        int width = getWidth() - shadowSize * 2;
        int height = getHeight() - shadowSize * 2;
        g2.fillRect(x, y, width, height);
        g2.dispose();
        super.paint(graphics);
    }

    @Override
    public void setBounds(int i, int i1, int i2, int i3) {
        super.setBounds(i, i1, i2, i3);
        createImageShadow();
    }

    private void createImageShadow() {
        imageShadow = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = imageShadow.createGraphics();
        g2.drawImage(createShadow(), 0, 0, null);
        g2.dispose();
    }

    private BufferedImage createShadow() {
        BufferedImage img = new BufferedImage(getWidth() - shadowSize * 2, getHeight() - shadowSize * 2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.fillRect(0, 0, img.getWidth(), img.getHeight());
        g2.dispose();
        return new ShadowRenderer(shadowSize, 0.3f, new Color(100, 100, 100)).createShadow(img);
    }
}
