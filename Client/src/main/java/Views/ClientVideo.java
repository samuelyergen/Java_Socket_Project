package Views;

import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

/**
 * Frame used to stream
 */
public class ClientVideo extends JFrame {
    EmbeddedMediaPlayerComponent mediaPlayer;
    public ClientVideo(String url) {
        JPanel panVideo = new JPanel();

        mediaPlayer = new EmbeddedMediaPlayerComponent();
        this.setBounds(new Rectangle(200, 200, 800, 600));
        panVideo.setLayout(new BorderLayout());
        panVideo.add(mediaPlayer);

        //Button play/pause
        JButton btnPausePlay = new JButton("Pause/Play");
        btnPausePlay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mediaPlayer.mediaPlayer().controls().setPause(mediaPlayer.mediaPlayer().status().isPlaying());
            }
        });
        this.add(btnPausePlay, BorderLayout.SOUTH);

        this.add(panVideo);
        this.setVisible(true);
        //Closing the windows
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                mediaPlayer.mediaPlayer().release();
            }
        });

        mediaPlayer.mediaPlayer().media().play(url);


    }
}
