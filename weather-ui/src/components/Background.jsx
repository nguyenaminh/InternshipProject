export default function Background() {
  return (
    <>
      <div
        style={{
          position: "fixed",
          top: 0,
          left: 0,
          width: "100vw",
          height: "100vh",
          zIndex: 0,
          pointerEvents: "none",
          background:
            "radial-gradient(ellipse at bottom, #1b2735 0%, #090a0f 100%)",
        }}
      />
      <div
        aria-hidden="true"
        style={{
          position: "fixed",
          top: 0,
          left: 0,
          width: "100vw",
          height: "100vh",
          zIndex: 0,
          pointerEvents: "none",
          backgroundImage: `
            radial-gradient(2px 2px at 20px 30px, white, transparent),
            radial-gradient(1.5px 1.5px at 50px 70px, white, transparent),
            radial-gradient(1.8px 1.8px at 120px 130px, white, transparent),
            radial-gradient(2.5px 2.5px at 200px 80px, white, transparent),
            radial-gradient(1.2px 1.2px at 300px 200px, white, transparent),
            radial-gradient(1.6px 1.6px at 350px 250px, white, transparent),
            radial-gradient(1.7px 1.7px at 400px 300px, white, transparent),
            radial-gradient(2px 2px at 450px 100px, white, transparent)
          `,
          animation: "twinkle 4s infinite alternate",
          opacity: 0.7,
        }}
      />
      <style>{`
        @keyframes twinkle {
          0% { opacity: 0.4; }
          100% { opacity: 0.8; }
        }
      `}</style>
    </>
  );
}
