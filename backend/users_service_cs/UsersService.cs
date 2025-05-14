public static class UsersService
{
    public static void Main(string[] args)
    {
        var builder = WebApplication.CreateBuilder(args);
        builder.Services.AddControllers();
        var app = builder.Build();

        app.UseHttpsRedirection();

        app.MapGet("/users", () =>
        {
            return new [] { "admin", "User1", "User2" };
        });

        app.MapControllers();
        app.Run();
    }
}
