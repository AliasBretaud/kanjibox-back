--
-- TOC entry 216 (class 1259 OID 16386)
-- Name: kanji; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.kanji (
    id bigint NOT NULL,
    time_stamp timestamp(6) without time zone,
    value character varying(255)
);


--
-- TOC entry 215 (class 1259 OID 16385)
-- Name: kanji_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.kanji_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3383 (class 0 OID 0)
-- Dependencies: 215
-- Name: kanji_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.kanji_id_seq OWNED BY public.kanji.id;


--
-- TOC entry 217 (class 1259 OID 16392)
-- Name: kanji_kun_yomi; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.kanji_kun_yomi (
    kanji_id bigint NOT NULL,
    kun_yomi character varying(255)
);


--
-- TOC entry 218 (class 1259 OID 16395)
-- Name: kanji_on_yomi; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.kanji_on_yomi (
    kanji_id bigint NOT NULL,
    on_yomi character varying(255)
);


--
-- TOC entry 219 (class 1259 OID 16398)
-- Name: kanji_translation; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.kanji_translation (
    kanji_id bigint NOT NULL,
    translation character varying(255)
);


--
-- TOC entry 221 (class 1259 OID 16402)
-- Name: word; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.word (
    id bigint NOT NULL,
    furigana_value character varying(255),
    time_stamp timestamp(6) without time zone,
    translation character varying(255),
    value character varying(255)
);


--
-- TOC entry 220 (class 1259 OID 16401)
-- Name: word_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.word_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3384 (class 0 OID 0)
-- Dependencies: 220
-- Name: word_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.word_id_seq OWNED BY public.word.id;


--
-- TOC entry 222 (class 1259 OID 16410)
-- Name: word_kanji; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.word_kanji (
    word_id bigint NOT NULL,
    kanji_id bigint NOT NULL
);


--
-- TOC entry 3224 (class 2604 OID 16389)
-- Name: kanji id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.kanji ALTER COLUMN id SET DEFAULT nextval('public.kanji_id_seq'::regclass);


--
-- TOC entry 3225 (class 2604 OID 16405)
-- Name: word id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.word ALTER COLUMN id SET DEFAULT nextval('public.word_id_seq'::regclass);


--
-- TOC entry 3227 (class 2606 OID 16391)
-- Name: kanji kanji_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.kanji
    ADD CONSTRAINT kanji_pkey PRIMARY KEY (id);


--
-- TOC entry 3229 (class 2606 OID 16409)
-- Name: word word_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.word
    ADD CONSTRAINT word_pkey PRIMARY KEY (id);


--
-- TOC entry 3232 (class 2606 OID 16423)
-- Name: kanji_translation fk588vmti9fq13gj8pd4cgt5e4d; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.kanji_translation
    ADD CONSTRAINT fk588vmti9fq13gj8pd4cgt5e4d FOREIGN KEY (kanji_id) REFERENCES public.kanji(id);


--
-- TOC entry 3233 (class 2606 OID 16428)
-- Name: word_kanji fk5l2xit9ourucp8n17oas3yh3i; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.word_kanji
    ADD CONSTRAINT fk5l2xit9ourucp8n17oas3yh3i FOREIGN KEY (kanji_id) REFERENCES public.kanji(id);


--
-- TOC entry 3234 (class 2606 OID 16433)
-- Name: word_kanji fk9k0ibk7nwps4iwreqe2b5ki61; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.word_kanji
    ADD CONSTRAINT fk9k0ibk7nwps4iwreqe2b5ki61 FOREIGN KEY (word_id) REFERENCES public.word(id);


--
-- TOC entry 3231 (class 2606 OID 16418)
-- Name: kanji_on_yomi fkj3rl38wyq6bb0vivtpsra2694; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.kanji_on_yomi
    ADD CONSTRAINT fkj3rl38wyq6bb0vivtpsra2694 FOREIGN KEY (kanji_id) REFERENCES public.kanji(id);


--
-- TOC entry 3230 (class 2606 OID 16413)
-- Name: kanji_kun_yomi fklff2p4x1taqtuicrhfc5fds0i; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.kanji_kun_yomi
    ADD CONSTRAINT fklff2p4x1taqtuicrhfc5fds0i FOREIGN KEY (kanji_id) REFERENCES public.kanji(id);


-- Completed on 2023-12-08 17:06:53

--
-- PostgreSQL database dump complete
--

