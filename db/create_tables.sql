--
-- TOC entry 215 (class 1259 OID 25477)
-- Name: kanji; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.kanji (
    id bigint NOT NULL,
    time_stamp timestamp(6) without time zone,
    value character varying(255),
    user_id bigint
);


--
-- TOC entry 216 (class 1259 OID 25480)
-- Name: kanji_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.kanji_id_seq
    START WITH 194
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4879 (class 0 OID 0)
-- Dependencies: 216
-- Name: kanji_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.kanji_id_seq OWNED BY public.kanji.id;


--
-- TOC entry 217 (class 1259 OID 25481)
-- Name: kanji_kun_yomi; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.kanji_kun_yomi (
    kanji_id bigint NOT NULL,
    kun_yomi character varying(255)
);


--
-- TOC entry 218 (class 1259 OID 25484)
-- Name: kanji_on_yomi; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.kanji_on_yomi (
    kanji_id bigint NOT NULL,
    on_yomi character varying(255)
);


--
-- TOC entry 219 (class 1259 OID 25487)
-- Name: kanji_translation; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.kanji_translation (
    kanji_id bigint NOT NULL,
    translation_lang character varying(255),
    translation_label character varying(255),
    CONSTRAINT kanji_translation_translation_lang_check CHECK (((translation_lang)::text = ANY (ARRAY[('JA'::character varying)::text, ('EN'::character varying)::text, ('FR'::character varying)::text])))
);


--
-- TOC entry 225 (class 1259 OID 25558)
-- Name: users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.users (
    id bigint NOT NULL,
    last_connected timestamp(6) without time zone,
    sub character varying(255) NOT NULL
);


--
-- TOC entry 224 (class 1259 OID 25557)
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.users_id_seq
    START WITH 2
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4880 (class 0 OID 0)
-- Dependencies: 224
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- TOC entry 220 (class 1259 OID 25493)
-- Name: word; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.word (
    id bigint NOT NULL,
    furigana_value character varying(255),
    time_stamp timestamp(6) without time zone,
    value character varying(255),
    user_id bigint
);


--
-- TOC entry 221 (class 1259 OID 25498)
-- Name: word_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.word_id_seq
    START WITH 193
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4881 (class 0 OID 0)
-- Dependencies: 221
-- Name: word_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.word_id_seq OWNED BY public.word.id;


--
-- TOC entry 222 (class 1259 OID 25499)
-- Name: word_kanji; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.word_kanji (
    word_id bigint NOT NULL,
    kanji_id bigint NOT NULL
);


--
-- TOC entry 223 (class 1259 OID 25502)
-- Name: word_translation; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.word_translation (
    word_id bigint NOT NULL,
    translation_lang character varying(255),
    translation_label character varying(255),
    CONSTRAINT word_translation_translation_lang_check CHECK (((translation_lang)::text = ANY (ARRAY[('JA'::character varying)::text, ('EN'::character varying)::text, ('FR'::character varying)::text])))
);


--
-- TOC entry 4710 (class 2604 OID 25508)
-- Name: kanji id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.kanji ALTER COLUMN id SET DEFAULT nextval('public.kanji_id_seq'::regclass);


--
-- TOC entry 4712 (class 2604 OID 25561)
-- Name: users id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- TOC entry 4711 (class 2604 OID 25509)
-- Name: word id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.word ALTER COLUMN id SET DEFAULT nextval('public.word_id_seq'::regclass);


--
-- TOC entry 4716 (class 2606 OID 25511)
-- Name: kanji kanji_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.kanji
    ADD CONSTRAINT kanji_pkey PRIMARY KEY (id);


--
-- TOC entry 4720 (class 2606 OID 25565)
-- Name: users uk_mtuf0eryuefwgkh9e3m5jdn8t; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk_mtuf0eryuefwgkh9e3m5jdn8t UNIQUE (sub);


--
-- TOC entry 4722 (class 2606 OID 25563)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- TOC entry 4718 (class 2606 OID 25513)
-- Name: word word_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.word
    ADD CONSTRAINT word_pkey PRIMARY KEY (id);


--
-- TOC entry 4726 (class 2606 OID 25514)
-- Name: kanji_translation fk588vmti9fq13gj8pd4cgt5e4d; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.kanji_translation
    ADD CONSTRAINT fk588vmti9fq13gj8pd4cgt5e4d FOREIGN KEY (kanji_id) REFERENCES public.kanji(id);


--
-- TOC entry 4728 (class 2606 OID 25519)
-- Name: word_kanji fk5l2xit9ourucp8n17oas3yh3i; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.word_kanji
    ADD CONSTRAINT fk5l2xit9ourucp8n17oas3yh3i FOREIGN KEY (kanji_id) REFERENCES public.kanji(id);


--
-- TOC entry 4723 (class 2606 OID 25566)
-- Name: kanji fk947ianvyeyanaq4l236g8uunq; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.kanji
    ADD CONSTRAINT fk947ianvyeyanaq4l236g8uunq FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- TOC entry 4729 (class 2606 OID 25524)
-- Name: word_kanji fk9k0ibk7nwps4iwreqe2b5ki61; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.word_kanji
    ADD CONSTRAINT fk9k0ibk7nwps4iwreqe2b5ki61 FOREIGN KEY (word_id) REFERENCES public.word(id);


--
-- TOC entry 4725 (class 2606 OID 25529)
-- Name: kanji_on_yomi fkj3rl38wyq6bb0vivtpsra2694; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.kanji_on_yomi
    ADD CONSTRAINT fkj3rl38wyq6bb0vivtpsra2694 FOREIGN KEY (kanji_id) REFERENCES public.kanji(id);


--
-- TOC entry 4724 (class 2606 OID 25534)
-- Name: kanji_kun_yomi fklff2p4x1taqtuicrhfc5fds0i; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.kanji_kun_yomi
    ADD CONSTRAINT fklff2p4x1taqtuicrhfc5fds0i FOREIGN KEY (kanji_id) REFERENCES public.kanji(id);


--
-- TOC entry 4727 (class 2606 OID 25571)
-- Name: word fkp3ml0u5r7pipahos74qeyjtvg; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.word
    ADD CONSTRAINT fkp3ml0u5r7pipahos74qeyjtvg FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- TOC entry 4730 (class 2606 OID 25539)
-- Name: word_translation fkq8fyvjn6930wey96qy5xfutlw; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.word_translation
    ADD CONSTRAINT fkq8fyvjn6930wey96qy5xfutlw FOREIGN KEY (word_id) REFERENCES public.word(id);


-- Completed on 2024-04-26 15:27:50

--
-- PostgreSQL database dump complete
--

