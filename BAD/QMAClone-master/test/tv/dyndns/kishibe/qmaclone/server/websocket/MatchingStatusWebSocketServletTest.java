package tv.dyndns.kishibe.qmaclone.server.websocket;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.InetSocketAddress;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import tv.dyndns.kishibe.qmaclone.client.constant.Constant;
import tv.dyndns.kishibe.qmaclone.client.packet.PacketMatchingStatus;
import tv.dyndns.kishibe.qmaclone.server.Game;
import tv.dyndns.kishibe.qmaclone.server.GameManager;
import tv.dyndns.kishibe.qmaclone.server.exception.GameNotFoundException;
import tv.dyndns.kishibe.qmaclone.server.websocket.MatchingStatusWebSocketServlet.MatchingStatusWebSocket;

@RunWith(MockitoJUnitRunner.class)
public class MatchingStatusWebSocketServletTest {
  @Mock
  private GameManager mockGameManager;
  @Mock
  private Session mockSession;
  @Mock
  private MessageSender<PacketMatchingStatus> mockMessageSender;
  @Mock
  private WebSocketServletFactory mockWebSocketServletFactory;
  @Mock
  private Game mockGame;
  private FakeUpgradeRequest fakeUpgradeRequest;
  private MatchingStatusWebSocketServlet servlet;
  private MatchingStatusWebSocket webSocket;

  @Before
  public void setUp() throws Exception {
    servlet = new MatchingStatusWebSocketServlet(mockGameManager);
    webSocket = new MatchingStatusWebSocket();
    fakeUpgradeRequest = new FakeUpgradeRequest();
  }

  @Test
  public void onConnectJoinsSessionToMatchingStatusMessageSender() throws Exception {
    fakeUpgradeRequest
        .setParameterMap(ImmutableMap.of(Constant.KEY_GAME_SESSION_ID, ImmutableList.of("121")));

    when(mockSession.getUpgradeRequest()).thenReturn(fakeUpgradeRequest);
    when(mockGameManager.getSession(121)).thenReturn(mockGame);
    when(mockGame.getMatchingStatusMessageSender()).thenReturn(mockMessageSender);

    webSocket.onConnect(mockSession);

    verify(mockMessageSender).join(mockSession);
  }

  @Test
  public void onConnectClosesSessionIfFailedToParseGameSessionId() throws Exception {
    fakeUpgradeRequest.setParameterMap(ImmutableMap.of());

    when(mockSession.getUpgradeRequest()).thenReturn(fakeUpgradeRequest);
    when(mockGameManager.getSession(121)).thenReturn(mockGame);
    when(mockGame.getMatchingStatusMessageSender()).thenReturn(mockMessageSender);

    webSocket.onConnect(mockSession);

    verify(mockSession).close();
  }

  @Test
  public void onConnectClosesSessionIfGameSessionNotFound() throws Exception {
    fakeUpgradeRequest.setParameterMap(ImmutableMap.of());

    when(mockSession.getUpgradeRequest()).thenReturn(fakeUpgradeRequest);
    when(mockGameManager.getSession(121)).thenThrow(new GameNotFoundException());

    webSocket.onConnect(mockSession);

    verify(mockSession).close();
  }

  @Test
  public void onCloseByesSessionFromMatchingStatusMessageSender() throws Exception {
    fakeUpgradeRequest
        .setParameterMap(ImmutableMap.of(Constant.KEY_GAME_SESSION_ID, ImmutableList.of("121")));

    when(mockSession.getUpgradeRequest()).thenReturn(fakeUpgradeRequest);
    when(mockGameManager.getSession(121)).thenReturn(mockGame);
    when(mockGame.getMatchingStatusMessageSender()).thenReturn(mockMessageSender);

    webSocket.onConnect(mockSession);
    webSocket.onClose(0, "");

    verify(mockMessageSender).bye(mockSession);
  }

  @Test
  public void onCloseClosesSessionIfGameSessionNotFound() throws Exception {
    fakeUpgradeRequest
        .setParameterMap(ImmutableMap.of(Constant.KEY_GAME_SESSION_ID, ImmutableList.of("121")));

    when(mockSession.getUpgradeRequest()).thenReturn(fakeUpgradeRequest);
    when(mockGameManager.getSession(121)).thenThrow(new GameNotFoundException());

    webSocket.onConnect(mockSession);
    webSocket.onClose(0, "");

    verify(mockSession, times(2)).close();
  }

  @Test
  public void onErrorByesSessionFromMatchingStatusMessageSender() throws Exception {
    fakeUpgradeRequest
        .setParameterMap(ImmutableMap.of(Constant.KEY_GAME_SESSION_ID, ImmutableList.of("121")));

    when(mockSession.getUpgradeRequest()).thenReturn(fakeUpgradeRequest);
    when(mockSession.getRemoteAddress()).thenReturn(new InetSocketAddress(0));
    when(mockGameManager.getSession(121)).thenReturn(mockGame);
    when(mockGame.getMatchingStatusMessageSender()).thenReturn(mockMessageSender);

    webSocket.onConnect(mockSession);
    webSocket.onError(new Exception());

    verify(mockMessageSender).bye(mockSession);
  }

  @Test
  public void onErrorClosesSessionIfGameSessionNotFound() throws Exception {
    fakeUpgradeRequest
        .setParameterMap(ImmutableMap.of(Constant.KEY_GAME_SESSION_ID, ImmutableList.of("121")));

    when(mockSession.getUpgradeRequest()).thenReturn(fakeUpgradeRequest);
    when(mockGameManager.getSession(121)).thenThrow(new GameNotFoundException());
    when(mockSession.getRemoteAddress()).thenReturn(new InetSocketAddress(0));

    webSocket.onConnect(mockSession);
    webSocket.onError(new Exception());

    verify(mockSession, times(2)).close();
  }

  @Test
  public void configureRegistersMatchingStatusWebSocket() {
    servlet.configure(mockWebSocketServletFactory);

    verify(mockWebSocketServletFactory).register(MatchingStatusWebSocket.class);
  }
}
