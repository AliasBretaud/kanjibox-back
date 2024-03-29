--
-- TOC entry 216 (class 1259 OID 25269)
-- Name: kanji; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.kanji (
    id bigint NOT NULL,
    time_stamp timestamp(6) without time zone,
    value character varying(255)
);


--
-- TOC entry 215 (class 1259 OID 25268)
-- Name: kanji_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.kanji_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4875 (class 0 OID 0)
-- Dependencies: 215
-- Name: kanji_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.kanji_id_seq OWNED BY public.kanji.id;


--
-- TOC entry 217 (class 1259 OID 25275)
-- Name: kanji_kun_yomi; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.kanji_kun_yomi (
    kanji_id bigint NOT NULL,
    kun_yomi character varying(255)
);


--
-- TOC entry 218 (class 1259 OID 25278)
-- Name: kanji_on_yomi; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.kanji_on_yomi (
    kanji_id bigint NOT NULL,
    on_yomi character varying(255)
);


--
-- TOC entry 219 (class 1259 OID 25281)
-- Name: kanji_translation; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.kanji_translation (
    kanji_id bigint NOT NULL,
    translation_lang character varying(255),
    translation_label character varying(255),
    CONSTRAINT kanji_translation_translation_lang_check CHECK (((translation_lang)::text = ANY ((ARRAY['JA'::character varying, 'EN'::character varying, 'FR'::character varying])::text[])))
);


--
-- TOC entry 221 (class 1259 OID 25288)
-- Name: word; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.word (
    id bigint NOT NULL,
    furigana_value character varying(255),
    time_stamp timestamp(6) without time zone,
    value character varying(255)
);


--
-- TOC entry 220 (class 1259 OID 25287)
-- Name: word_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.word_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4876 (class 0 OID 0)
-- Dependencies: 220
-- Name: word_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.word_id_seq OWNED BY public.word.id;


--
-- TOC entry 222 (class 1259 OID 25296)
-- Name: word_kanji; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.word_kanji (
    word_id bigint NOT NULL,
    kanji_id bigint NOT NULL
);

--
-- TOC entry 223 (class 1259 OID 25299)
-- Name: word_translation; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.word_translation (
    word_id bigint NOT NULL,
    translation_lang character varying(255),
    translation_label character varying(255),
    CONSTRAINT word_translation_translation_lang_check CHECK (((translation_lang)::text = ANY ((ARRAY['JA'::character varying, 'EN'::character varying, 'FR'::character varying])::text[])))
);


--
-- TOC entry 4713 (class 2604 OID 25272)
-- Name: kanji id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.kanji ALTER COLUMN id SET DEFAULT nextval('public.kanji_id_seq'::regclass);


--
-- TOC entry 4714 (class 2604 OID 25291)
-- Name: word id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.word ALTER COLUMN id SET DEFAULT nextval('public.word_id_seq'::regclass);


--
-- TOC entry 4718 (class 2606 OID 25274)
-- Name: kanji kanji_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.kanji
    ADD CONSTRAINT kanji_pkey PRIMARY KEY (id);


--
-- TOC entry 4720 (class 2606 OID 25295)
-- Name: word word_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.word
    ADD CONSTRAINT word_pkey PRIMARY KEY (id);


--
-- TOC entry 4723 (class 2606 OID 25315)
-- Name: kanji_translation fk588vmti9fq13gj8pd4cgt5e4d; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.kanji_translation
    ADD CONSTRAINT fk588vmti9fq13gj8pd4cgt5e4d FOREIGN KEY (kanji_id) REFERENCES public.kanji(id);


--
-- TOC entry 4724 (class 2606 OID 25320)
-- Name: word_kanji fk5l2xit9ourucp8n17oas3yh3i; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.word_kanji
    ADD CONSTRAINT fk5l2xit9ourucp8n17oas3yh3i FOREIGN KEY (kanji_id) REFERENCES public.kanji(id);


--
-- TOC entry 4725 (class 2606 OID 25325)
-- Name: word_kanji fk9k0ibk7nwps4iwreqe2b5ki61; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.word_kanji
    ADD CONSTRAINT fk9k0ibk7nwps4iwreqe2b5ki61 FOREIGN KEY (word_id) REFERENCES public.word(id);


--
-- TOC entry 4722 (class 2606 OID 25310)
-- Name: kanji_on_yomi fkj3rl38wyq6bb0vivtpsra2694; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.kanji_on_yomi
    ADD CONSTRAINT fkj3rl38wyq6bb0vivtpsra2694 FOREIGN KEY (kanji_id) REFERENCES public.kanji(id);


--
-- TOC entry 4721 (class 2606 OID 25305)
-- Name: kanji_kun_yomi fklff2p4x1taqtuicrhfc5fds0i; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.kanji_kun_yomi
    ADD CONSTRAINT fklff2p4x1taqtuicrhfc5fds0i FOREIGN KEY (kanji_id) REFERENCES public.kanji(id);


--
-- TOC entry 4726 (class 2606 OID 25330)
-- Name: word_translation fkq8fyvjn6930wey96qy5xfutlw; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.word_translation
    ADD CONSTRAINT fkq8fyvjn6930wey96qy5xfutlw FOREIGN KEY (word_id) REFERENCES public.word(id);


-- Completed on 2024-03-29 21:16:17

--
-- PostgreSQL database dump complete
--

