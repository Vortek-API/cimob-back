package fatec.vortek.cimob.application.service;

import fatec.vortek.cimob.domain.enums.TimelineAcao;
import fatec.vortek.cimob.domain.enums.TimelineTipo;
import fatec.vortek.cimob.domain.model.Indicador;
import fatec.vortek.cimob.domain.model.Timeline;
import fatec.vortek.cimob.domain.model.Usuario;
import fatec.vortek.cimob.domain.service.TimelineService;
import fatec.vortek.cimob.domain.service.UsuarioService;
import fatec.vortek.cimob.infrastructure.repository.TimelineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TimelineServiceImpl implements TimelineService {

    private final TimelineRepository repository;
    private final UsuarioService usuarioService;

    @Override
    public Timeline criar(Timeline timeline) {
        return repository.save(timeline);
    }

    @Override
    public Timeline atualizar(Timeline timeline) {
        return repository.save(timeline);
    }

    @Override
    public Timeline buscarPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<Timeline> listarTodos() {
        return repository.findAll();
    }

    @Override
    public Timeline criarTimelineLogin(Usuario usuario) {
        Timeline timeline = new Timeline();
        timeline.setAcao(TimelineAcao.LOGIN);
        timeline.setDescricao("Usuário realizou login no sistema.");
        timeline.setData(java.time.LocalDateTime.now());
        timeline.setUsuario(usuario);
        timeline.setTipo(TimelineTipo.USUARIO);

        return criar(timeline);
    }

    @Override
    public Timeline criarTimelineLogout(Usuario usuario) {
        Timeline timeline = new Timeline();
        timeline.setAcao(TimelineAcao.LOGOUT);
        timeline.setDescricao("Usuário realizou logout do sistema.");
        timeline.setData(java.time.LocalDateTime.now());
        timeline.setUsuario(usuario);
        timeline.setTipo(TimelineTipo.USUARIO);

        return criar(timeline);
    }

    @Override
    public Timeline criarTimelineIndicador(String descricao)
    {
        Timeline timeline = new Timeline();
        timeline.setAcao(TimelineAcao.ALTERACAO);
        timeline.setDescricao(descricao);
        timeline.setData(java.time.LocalDateTime.now());
        timeline.setTipo(TimelineTipo.INDICADOR);
        timeline.setUsuario(usuarioService.getUsuarioLogado());

        return criar(timeline);
    }

    @Override
    public Timeline criarTimelineIndicadorOculto(Indicador indicador)
    {
        return criarTimelineIndicador(String.format("O indicador '%s' foi ocultado.", indicador.getNome()));
    }

    @Override
    public Timeline criarTimelineIndicadorDesoculto(Indicador indicador)
    {
        return criarTimelineIndicador(String.format("O indicador '%s' foi desocultado.", indicador.getNome()));
    }
}
